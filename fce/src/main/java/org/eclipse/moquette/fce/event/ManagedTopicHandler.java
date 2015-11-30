package org.eclipse.moquette.fce.event;

import java.util.logging.Logger;

import org.eclipse.moquette.fce.common.converter.QuotaConverter;
import org.eclipse.moquette.fce.exception.FceAuthorizationException;
import org.eclipse.moquette.fce.model.common.ManagedScope;
import org.eclipse.moquette.fce.model.common.ManagedTopic;
import org.eclipse.moquette.fce.model.common.ManagedZone;
import org.eclipse.moquette.fce.model.configuration.UserConfiguration;
import org.eclipse.moquette.fce.model.info.InfoMessageType;
import org.eclipse.moquette.fce.model.quota.UserQuota;
import org.eclipse.moquette.fce.service.IFceServiceFactory;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.MqttAction;

/**
 * Handler which is used for a topic request in a managed zone.
 * 
 * @author lants1
 *
 */
public class ManagedTopicHandler extends FceEventHandler {

	private final static Logger log = Logger.getLogger(ManagedTopicHandler.class.getName());

	public ManagedTopicHandler(IFceServiceFactory services, String pluginClientIdentifier) {
		super(services, pluginClientIdentifier);
	}

	public boolean canDoOperation(AuthorizationProperties props, MqttAction action) {
		log.info("recieved Event on:" + props.getTopic() + "from client:" + props.getClientId() + " and action:"
				+ action);

		Boolean preCheckState = preCheckManagedZone(props, action);

		if (preCheckState != null) {
			return preCheckState;
		}

		try {
			UserConfiguration configGlobal = getServices().getConfigDb(ManagedScope.GLOBAL).getConfiguration(props);
			UserQuota quotasGlobal = getServices().getQuotaDb(ManagedScope.GLOBAL).getQuota(props, action);

			if (configGlobal.isValidForEveryone()) {
				if (quotasGlobal == null) {
					quotasGlobal = new UserQuota(props.getUser(), props.getUser(), action,
							QuotaConverter.convertRestrictions(configGlobal.getRestrictions(action)));
				}
			}

			if (!configGlobal.isValid(props, action)) {
				logAndSendInfoMsg(InfoMessageType.GLOBAL_CONFIG_REJECTED, props, action);
				return false;
			}

			if (!quotasGlobal.isValid(props, action)) {
				logAndSendInfoMsg(InfoMessageType.GLOBAL_QUOTA_DEPLETED, props, action);
				return false;
			}

			UserConfiguration configPrivate = getServices().getConfigDb(ManagedScope.PRIVATE).getConfiguration(props);
			UserQuota quotasPrivate = getServices().getQuotaDb(ManagedScope.PRIVATE).getQuota(props, action);

			if (configPrivate != null) {
				if (!configPrivate.isValid(props, action)) {
					logAndSendInfoMsg(InfoMessageType.PRIVATE_CONFIG_REJECTED, props, action);
					return false;
				}

				if (!quotasPrivate.isValid(props, action)) {
					logAndSendInfoMsg(InfoMessageType.PRIVATE_QUOTA_DEPLETED, props, action);
					return false;
				}
				substractQuota(props, action, ManagedZone.QUOTA_PRIVATE, quotasPrivate);
			}

			substractQuota(props, action, ManagedZone.QUOTA_GLOBAL, quotasGlobal);
			log.info("accepted Event on:" + props.getTopic() + "from client:" + props.getClientId() + " and action:"
					+ action);
			return true;
		} catch (FceAuthorizationException e) {
			logAndSendInfoMsg(InfoMessageType.AUTHORIZATION_EXCEPTION, props, action);
			return false;
		}
	}

	private void substractQuota(AuthorizationProperties properties, MqttAction operation, ManagedZone zone,
			UserQuota userQuotas) throws FceAuthorizationException {
		userQuotas.substractRequestFromQuota(properties, operation);
		String quotaJson = getServices().getJsonParser().serialize(userQuotas);

		String userTopicIdentifier = new ManagedTopic(properties.getTopic()).getIdentifier(properties, zone, operation);

		getServices().getQuotaDb(zone.getScope()).put(userTopicIdentifier, userQuotas);
		getServices().getMqtt().publish(userTopicIdentifier, quotaJson);
	}

}
