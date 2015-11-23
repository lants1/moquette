package org.eclipse.moquette.fce.event;

import java.util.logging.Logger;

import org.eclipse.moquette.fce.common.ManagedZone;
import org.eclipse.moquette.fce.exception.FceAuthorizationException;
import org.eclipse.moquette.fce.model.ManagedTopic;
import org.eclipse.moquette.fce.model.configuration.UserConfiguration;
import org.eclipse.moquette.fce.model.quota.UserQuota;
import org.eclipse.moquette.fce.service.IFceServiceFactory;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.MqttAction;

public class ManagedTopicHandler extends FceEventHandler {

	private final static Logger log = Logger.getLogger(ManagedTopicHandler.class.getName());
	
	public ManagedTopicHandler(IFceServiceFactory services, String pluginClientIdentifier) {
		super(services, pluginClientIdentifier);
	}

	public boolean canDoOperation(AuthorizationProperties properties, MqttAction operation) {
		log.fine("recieved canRead Event on " + properties.getTopic() + "from client" + properties.getClientId());

		if (properties.getAnonymous()) {
			return Boolean.FALSE;
		}

		// we are in a managed zone
		try {
			UserConfiguration userConfig = services.getConfigDb().getConfiguration(properties);
			UserQuota userQuotas = services.getQuotaDb().getQuota(properties, operation);

			if (!userConfig.isValid(properties, operation) || !userQuotas.isValid(properties, operation)) {
				return false;
			}

			userQuotas.substractRequestFromQuota(properties, operation);
			String quotaJson = services.getJsonParser().serialize(userQuotas);

			String userTopicIdentifier = new ManagedTopic(properties.getTopic()).getIdentifier(properties,
					ManagedZone.QUOTA_GLOBAL, operation);

			services.getQuotaDb().put(userTopicIdentifier, userQuotas);
			services.getMqtt().publish(userTopicIdentifier, quotaJson);

			return true;
		} catch (FceAuthorizationException e) {
			log.warning(e.toString());
			// TODO lants1 publish info message
			// services.getMqttService().publish(topic, json, retained);
			return false;
		}
	}
	
}
