package org.eclipse.moquette.fce.event;

import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.moquette.fce.common.ManagedZone;
import org.eclipse.moquette.fce.common.converter.QuotaConverter;
import org.eclipse.moquette.fce.exception.FceAuthorizationException;
import org.eclipse.moquette.fce.model.ManagedScope;
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
			UserConfiguration userConfigGlobal = services.getConfigDb(ManagedScope.GLOBAL).getConfiguration(properties);
			UserQuota userQuotasGlobal = services.getQuotaDb(ManagedScope.GLOBAL).getQuota(properties, operation);

			if (StringUtils.isEmpty(userConfigGlobal.getUserIdentifier())) {
				if(userQuotasGlobal == null){
					userQuotasGlobal = new UserQuota(properties.getUser(), properties.getUser(), QuotaConverter.convertRestrictions(userConfigGlobal.getRestrictions(operation)));
				}
			}
			
			if (!userConfigGlobal.isValid(properties, operation) || !userQuotasGlobal.isValid(properties, operation)) {
				return false;
			}

			UserConfiguration userConfigPrivate = services.getConfigDb(ManagedScope.PRIVATE)
					.getConfiguration(properties);
			UserQuota userQuotasPrivate = services.getQuotaDb(ManagedScope.PRIVATE).getQuota(properties, operation);

			if (!userConfigPrivate.isValid(properties, operation)
					|| !userQuotasPrivate.isValid(properties, operation)) {
				return false;
			}

		
			substractQuota(properties, operation, ManagedZone.QUOTA_GLOBAL, userQuotasGlobal);
			substractQuota(properties, operation, ManagedZone.QUOTA_PRIVATE, userQuotasPrivate);

			return true;
		} catch (FceAuthorizationException e) {
			log.warning(e.toString());
			// TODO lants1 publish info message
			// services.getMqttService().publish(topic, json, retained);
			return false;
		}
	}
	
	private void substractQuota(AuthorizationProperties properties, MqttAction operation, ManagedZone zone, UserQuota userQuotas) throws FceAuthorizationException{
		userQuotas.substractRequestFromQuota(properties, operation);
		String quotaJson = services.getJsonParser().serialize(userQuotas);

		String userTopicIdentifier = new ManagedTopic(properties.getTopic()).getIdentifier(properties,
				zone, operation);

		services.getQuotaDb(zone.getScope()).put(userTopicIdentifier, userQuotas);
		services.getMqtt().publish(userTopicIdentifier, quotaJson);

	}

}
