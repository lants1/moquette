package io.moquette.fce.event;

import java.util.logging.Logger;

import org.apache.commons.codec.binary.StringUtils;

import io.moquette.fce.common.converter.QuotaConverter;
import io.moquette.fce.context.FceContext;
import io.moquette.fce.exception.FceAuthorizationException;
import io.moquette.fce.model.ManagedInformation;
import io.moquette.fce.model.common.CheckResult;
import io.moquette.fce.model.common.ManagedTopic;
import io.moquette.fce.model.common.ManagedZone;
import io.moquette.fce.model.common.TopicPermission;
import io.moquette.fce.model.configuration.UserConfiguration;
import io.moquette.fce.model.info.InfoMessage;
import io.moquette.fce.model.info.InfoMessageType;
import io.moquette.fce.model.quota.UserQuota;
import io.moquette.fce.service.FceServiceFactory;
import io.moquette.plugin.AuthorizationProperties;
import io.moquette.plugin.MqttAction;

/**
 * Abstract handler for each Managed Event with common methods...
 * 
 * @author lants1
 *
 */
public abstract class FceEventHandler {

	private static final Logger LOGGER = Logger.getLogger(FceEventHandler.class.getName());

	private FceContext context;
	private FceServiceFactory services;

	public FceEventHandler(FceContext context, FceServiceFactory services) {
		this.services = services;
		this.context = context;
	}

	public FceContext getContext() {
		return context;
	}

	public FceServiceFactory getServices() {
		return services;
	}

	public CheckResult preCheckManagedZone(AuthorizationProperties properties, MqttAction action) {
		if (properties.getAnonymous()) {
			LOGGER.info("anonymous login, authorization rejected");
			return CheckResult.INVALID;
		}

		if (isPluginClient(properties)) {
			LOGGER.info("can do operation:" + action + " for topic:" + properties.getTopic()
					+ " because it's plugin client: " + properties.getUser());
			return CheckResult.VALID;
		}
		if (!TopicPermission.getBasicPermission(properties.getTopic()).isAllowed(action)) {
			LOGGER.info("no permission to " + action + " topic" + properties.getTopic());
			return CheckResult.INVALID;
		}
		return CheckResult.NO_RESULT;
	}
	
	public void injectFceEnvironment(FceContext context, FceServiceFactory services) {
		this.context = context;
		this.services = services;
	}

	public abstract boolean canDoOperation(AuthorizationProperties properties, MqttAction operation);

	protected void sendInfoMessage(InfoMessageType msgType, AuthorizationProperties props, MqttAction action) {
		InfoMessage infoMsg = new InfoMessage(props.getClientId(),
				getContext().getHashAssignment().get(props.getClientId()), msgType, "mqttaction: " + action);
		getServices().getMqtt().publish(
				new ManagedTopic(props.getTopic())
						.getIdentifier(getContext().getHashAssignment().get(props.getClientId()), ManagedZone.INFO),
				getServices().getJsonParser().serialize(infoMsg));
		LOGGER.info(infoMsg.toString());
	}
	
	protected void sendInfoMessage(InfoMessageType msgType, ManagedInformation info, String topic) {
		InfoMessage infoMsg = new InfoMessage(info.getAlias(),
				info.getUserHash(), msgType, "");
		getServices().getMqtt().publish(
				new ManagedTopic(topic)
						.getIdentifier(info.getUserHash(), ManagedZone.INFO),
				getServices().getJsonParser().serialize(infoMsg));
		LOGGER.info(infoMsg.toString());
	}

	protected void storeNewQuotaForUserConfiguration(ManagedTopic topic, UserConfiguration usrConfig,
			ManagedZone quotaZone) throws FceAuthorizationException {
		// store the quota only when its has a userHash "not everyone config"
		if (usrConfig.getUserHash() != null) {
			UserQuota subQuota = QuotaConverter.convertSubscribeConfiguration(usrConfig);
			UserQuota pubQuota = QuotaConverter.convertPublishConfiguration(usrConfig);

			String subQuotaTopic = topic.getIdentifier(subQuota, quotaZone, MqttAction.SUBSCRIBE);
			String pubQuotaTopic = topic.getIdentifier(pubQuota, quotaZone, MqttAction.PUBLISH);

			getContext().getQuotaStore(usrConfig.getManagedScope()).put(subQuotaTopic, subQuota, true);
			getContext().getQuotaStore(usrConfig.getManagedScope()).put(pubQuotaTopic, pubQuota, true);
			getServices().getMqtt().publish(subQuotaTopic, getServices().getJsonParser().serialize(subQuota));
			getServices().getMqtt().publish(pubQuotaTopic, getServices().getJsonParser().serialize(pubQuota));
		}
	}
	
	private boolean isPluginClient(AuthorizationProperties properties) {
		if (!context.getPluginPw().isEmpty()) {
			return StringUtils.equals(getContext().getHashAssignment().get(properties.getClientId()),
					context.getPluginPw());
		}
		return false;
	}

}
