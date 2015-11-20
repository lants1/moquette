package org.eclipse.moquette.fce.event;

import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.StringUtils;
import org.eclipse.moquette.fce.common.FceHashUtil;
import org.eclipse.moquette.fce.common.ManagedZone;
import org.eclipse.moquette.fce.exception.FceNoAuthorizationPossibleException;
import org.eclipse.moquette.fce.model.ManagedTopic;
import org.eclipse.moquette.fce.model.configuration.Restriction;
import org.eclipse.moquette.fce.model.configuration.UserConfiguration;
import org.eclipse.moquette.fce.model.quota.Quota;
import org.eclipse.moquette.fce.model.quota.QuotaState;
import org.eclipse.moquette.fce.service.FceServiceFactory;
import org.eclipse.moquette.plugin.AuthenticationProperties;
import org.eclipse.moquette.plugin.AuthorizationProperties;

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

	// TODO lants1 evil, evil.. refactor this methods...
	public boolean canWrite(AuthorizationProperties properties) {
		log.fine("recieved canWrite Event on " + properties.getTopic() + "from client" + properties.getClientId());
		Boolean preCheckResult = preCheckForManagedZone(properties);
		if (preCheckResult != null) {
			return preCheckResult.booleanValue();
		}

		// --- it is managed and user needs to validated by configuration and
		// quota
		UserConfiguration userConfig;
		Quota quota;

		try {
			userConfig = services.getConfigDbService().getConfiguration(properties);
			quota = services.getQuotaDbService().getPublishQuota(properties);

			List<Restriction> restrictions = userConfig.getPublishRestrictions();

			if (restrictions.isEmpty()) {
				return true;
			}

			for (Restriction restriction : restrictions) {
				if (!restriction.getWsdlUrl().isEmpty()) {
					if (!services.getXmlSchemaValidationService()
							.isValidXmlFileAccordingToSchema(properties.getMessage(), restriction.getWsdlUrl())) {
						return false;
					}
				}
			}

			if (!quota.isValid(properties)) {
				return false;
			}

			quota.substractRequestFromQuota(properties);
			String quotaJson = services.getJsonParser().serialize(quota);
			// TODO lants1 could be a user quota or a everyone quota
			services.getMqttService().publish(new ManagedTopic(properties.getTopic()).getUserTopicIdentifier(properties,
					ManagedZone.MANAGED_QUOTA), quotaJson, true);

			return true;
		} catch (FceNoAuthorizationPossibleException e) {
			log.warning(e.toString());
			return false;
		}
	}

	public boolean canRead(AuthorizationProperties properties) {
		log.fine("recieved canRead Event on " + properties.getTopic() + "from client" + properties.getClientId());
		Boolean preCheckResult = preCheckForManagedZone(properties);
		if (preCheckResult != null) {
			return preCheckResult.booleanValue();
		}

		UserConfiguration userConfig;
		Quota quota;

		try {
			userConfig = services.getConfigDbService().getConfiguration(properties);

			quota = services.getQuotaDbService().getSubscribeQuota(properties);

			List<Restriction> restrictions = userConfig.getSubscribeRestrictions();

			if (restrictions.isEmpty()) {
				return true;
			}

			for (Restriction restriction : restrictions) {
				if (!restriction.getWsdlUrl().isEmpty()) {
					if (!services.getXmlSchemaValidationService()
							.isValidXmlFileAccordingToSchema(properties.getMessage(), restriction.getWsdlUrl())) {
						return false;
					}
				}
			}

			if (!quota.isValid(properties)) {
				return false;
			}

			quota.substractRequestFromQuota(properties);
			String quotaJson = services.getJsonParser().serialize(quota);
			// TODO lants1 could be a user quota or a everyone quota
			services.getMqttService().publish(new ManagedTopic(properties.getTopic()).getUserTopicIdentifier(properties,
					ManagedZone.MANAGED_QUOTA), quotaJson, true);

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
