package org.eclipse.moquette.fce.event;

import java.util.logging.Logger;

import org.apache.commons.codec.binary.StringUtils;
import org.eclipse.moquette.fce.common.FceHashUtil;
import org.eclipse.moquette.fce.common.ManagedZone;
import org.eclipse.moquette.fce.exception.FceNoAuthorizationPossibleException;
import org.eclipse.moquette.fce.model.ManagedTopic;
import org.eclipse.moquette.fce.model.configuration.UserConfiguration;
import org.eclipse.moquette.fce.model.quota.UserQuotaData;
import org.eclipse.moquette.fce.service.FceServiceFactory;
import org.eclipse.moquette.plugin.AuthenticationProperties;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.MqttOperation;

public class PluginEventHandler {

	private final static Logger log = Logger.getLogger(PluginEventHandler.class.getName());

	FceServiceFactory services;
	String pluginClientIdentifer;

	public PluginEventHandler(FceServiceFactory services, String pluginClientIdentifier) {
		this.services = services;
		this.pluginClientIdentifer = pluginClientIdentifier;
	}

	public boolean checkValid(AuthenticationProperties props) {
		log.fine("recieved checkValid Event for " + props.getUsername());

		return FceHashUtil.validateClientIdHash(props);
	}

	public boolean canDoOperation(AuthorizationProperties properties, MqttOperation operation) {
		log.fine("recieved canRead Event on " + properties.getTopic() + "from client" + properties.getClientId());
		Boolean preCheckResult = preCheckForManagedZone(properties);
		if (preCheckResult != null) {
			return preCheckResult.booleanValue();
		}

		// we are in a managed zone
		try {
			UserConfiguration userConfig = services.getConfigDbService().getConfiguration(properties);
			UserQuotaData userQuotas = services.getQuotaDbService().getQuota(properties, operation);

			if (!userConfig.isValid(properties, operation) || !userQuotas.isValid(properties, operation)) {
				return false;
			}

			userQuotas.substractRequestFromQuota(properties, operation);
			String quotaJson = services.getJsonParser().serialize(userQuotas);

			String userTopicIdentifier = new ManagedTopic(properties.getTopic()).getUserTopicIdentifier(properties,
					ManagedZone.MANAGED_QUOTA, operation);

			services.getQuotaDbService().put(userTopicIdentifier, userQuotas, false);
			services.getMqttService().publish(userTopicIdentifier, quotaJson, true);

			return true;
		} catch (FceNoAuthorizationPossibleException e) {
			log.warning(e.toString());
			// TODO lants1 publish info message
			// services.getMqttService().publish(topic, json, retained);
			return false;
		}
	}

	private boolean isPluginClient(AuthorizationProperties properties) {
		if (!pluginClientIdentifer.isEmpty()) {
			return StringUtils.equals(properties.getClientId(), pluginClientIdentifer);
		}
		return false;
	}

	private Boolean preCheckForManagedZone(AuthorizationProperties properties) {
		if (isPluginClient(properties)) {
			return Boolean.TRUE;
		}

		if (!services.getAuthorizationService().getBasicPermission(properties.getTopic()).isWriteable()) {
			return Boolean.FALSE;
		}

		if (!services.getConfigDbService().isTopicFilterManaged(new ManagedTopic(properties.getTopic()))) {
			return Boolean.TRUE;
		}

		if (properties.getAnonymous()) {
			return Boolean.FALSE;
		}
		return null;
	}
}
