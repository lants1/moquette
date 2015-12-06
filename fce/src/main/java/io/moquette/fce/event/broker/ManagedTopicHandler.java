package io.moquette.fce.event.broker;

import java.util.logging.Level;
import java.util.logging.Logger;

import io.moquette.fce.common.converter.QuotaConverter;
import io.moquette.fce.context.FceContext;
import io.moquette.fce.event.FceEventHandler;
import io.moquette.fce.exception.FceAuthorizationException;
import io.moquette.fce.exception.FceSystemException;
import io.moquette.fce.model.common.CheckResult;
import io.moquette.fce.model.common.ManagedScope;
import io.moquette.fce.model.common.ManagedTopic;
import io.moquette.fce.model.common.ManagedZone;
import io.moquette.fce.model.configuration.UserConfiguration;
import io.moquette.fce.model.info.InfoMessageType;
import io.moquette.fce.model.quota.UserQuota;
import io.moquette.fce.service.FceServiceFactory;
import io.moquette.plugin.AuthorizationProperties;
import io.moquette.plugin.MqttAction;

/**
 * Handler which is used for a topic request in a managed zone.
 * 
 * @author lants1
 *
 */
public class ManagedTopicHandler extends FceEventHandler {

	private static final Logger LOGGER = Logger.getLogger(ManagedTopicHandler.class.getName());

	public ManagedTopicHandler(FceContext context, FceServiceFactory services) {
		super(context, services);
	}

	@Override
	public boolean canDoOperation(AuthorizationProperties props, MqttAction action) {
		String usernameHashFromRequest = getContext().getHashAssignment().get(props.getClientId());

		LOGGER.info("recieved Event on:" + props.getTopic() + "from client:" + props.getClientId() + " and action:"
				+ action);

		CheckResult preCheckState = preCheckManagedZone(props, action);

		if(!CheckResult.NO_RESULT.equals(preCheckState)){
			return preCheckState.getValue();
		}

		try {
			UserConfiguration configGlobal = getContext().getConfigurationStore(ManagedScope.GLOBAL)
					.getConfiguration(props.getTopic(), usernameHashFromRequest);
			UserQuota quotasGlobal = getContext().getQuotaStore(ManagedScope.GLOBAL).getQuota(props.getTopic(),
					usernameHashFromRequest, action);

			if (configGlobal.isValidForEveryone() && quotasGlobal == null) {
				quotasGlobal = new UserQuota("generated", usernameHashFromRequest, action,
						QuotaConverter.convertRestrictions(configGlobal.getRestrictions(action)));
			}

			if (!configGlobal.isValid(getServices(), props, action)) {
				sendInfoMessage(InfoMessageType.GLOBAL_CONFIG_REJECTED, props, action);
				return false;
			}
			
			if(quotasGlobal == null){
				throw new FceSystemException("illegal quota state null, something internal is wrong....");
			}

			if (!quotasGlobal.isValid(getServices(), props, action)) {
				sendInfoMessage(InfoMessageType.GLOBAL_QUOTA_DEPLETED, props, action);
				return false;
			}

			UserConfiguration configPrivate = getContext().getConfigurationStore(ManagedScope.PRIVATE)
					.getConfiguration(props.getTopic(), usernameHashFromRequest);
			UserQuota quotasPrivate = getContext().getQuotaStore(ManagedScope.PRIVATE).getQuota(props.getTopic(),
					usernameHashFromRequest, action);

			if (configPrivate != null) {
				if (!configPrivate.isValid(getServices(), props, action)) {
					sendInfoMessage(InfoMessageType.PRIVATE_CONFIG_REJECTED, props, action);
					return false;
				}

				if (!quotasPrivate.isValid(getServices(), props, action)) {
					sendInfoMessage(InfoMessageType.PRIVATE_QUOTA_DEPLETED, props, action);
					return false;
				}
				substractQuota(props, action, ManagedZone.QUOTA_PRIVATE, quotasPrivate);
			}

			substractQuota(props, action, ManagedZone.QUOTA_GLOBAL, quotasGlobal);
			LOGGER.info("accepted Event on:" + props.getTopic() + "from client:" + props.getClientId() + " and action:"
					+ action);
			return true;
		} catch (FceAuthorizationException e) {
			sendInfoMessage(InfoMessageType.AUTHORIZATION_EXCEPTION, props, action);
			LOGGER.log(Level.INFO, InfoMessageType.AUTHORIZATION_EXCEPTION + " for topic:" + props.getTopic() + " user: " + props.getUser() + " action:" + action, e);
			return false;
		}
	}

	private void substractQuota(AuthorizationProperties properties, MqttAction operation, ManagedZone zone,
			UserQuota userQuotas) throws FceAuthorizationException {
		String usernameHashFromRequest = getContext().getHashAssignment().get(properties.getClientId());

		userQuotas.substractRequestFromQuota(properties, operation);
		String quotaJson = getServices().getJsonParser().serialize(userQuotas);

		String userTopicIdentifier = new ManagedTopic(properties.getTopic()).getIdentifier(usernameHashFromRequest,
				zone, operation);

		getContext().getQuotaStore(zone.getScope()).put(userTopicIdentifier, userQuotas);
		getServices().getMqtt().publish(userTopicIdentifier, quotaJson);
	}

}
