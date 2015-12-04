package io.moquette.fce.event;

import java.util.logging.Logger;

import org.apache.commons.codec.binary.StringUtils;

import io.moquette.fce.common.converter.QuotaConverter;
import io.moquette.fce.context.FceContext;
import io.moquette.fce.exception.FceAuthorizationException;
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

	private final static Logger log = Logger.getLogger(FceEventHandler.class.getName());

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

	public Boolean preCheckManagedZone(AuthorizationProperties properties, MqttAction action) {
		if (properties.getAnonymous()) {
			log.info("anonymous login, authorization rejected");
			return Boolean.FALSE;
		}

		if (isPluginClient(properties)) {
			log.info("can do operation:" + action + " for topic:" + properties.getTopic()
					+ " because it's plugin client: " + properties.getUser());
			return Boolean.TRUE;
		}
		if (!TopicPermission.getBasicPermission(properties.getTopic()).isAllowed(action)) {
			log.info("no permission to " + action + " topic" + properties.getTopic());
			return Boolean.FALSE;
		}
		return null;
	}

	private boolean isPluginClient(AuthorizationProperties properties) {
		if (!context.getPluginPw().isEmpty()) {
			return StringUtils.equals(getContext().getHashAssignment().get(properties.getClientId()),
					context.getPluginPw());
		}
		return false;
	}

	protected void logAndSendInfoMsg(InfoMessageType msgType, AuthorizationProperties props, MqttAction action) {
		log.info(msgType + " for topic:" + props.getTopic() + " user: " + props.getUser() + " action:" + action);
		InfoMessage infoMsg = new InfoMessage(props.getClientId(), getContext().getHashAssignment().get(props.getClientId()), msgType, "mqttaction: " + action);
		getServices().getMqtt().publish(new ManagedTopic(props.getTopic()).getIdentifier(getContext().getHashAssignment().get(props.getClientId()), ManagedZone.INFO),
				getServices().getJsonParser().serialize(infoMsg));
	}

	protected void storeNewQuotaForUserConfiguration(ManagedTopic topic, UserConfiguration usrConfig,
			ManagedZone quotaZone) throws FceAuthorizationException {
		UserQuota subQuota = QuotaConverter.convertSubscribeConfiguration(usrConfig);
		UserQuota pubQuota = QuotaConverter.convertPublishConfiguration(usrConfig);

		String subQuotaTopic = topic.getIdentifier(subQuota, quotaZone, MqttAction.SUBSCRIBE);
		String pubQuotaTopic = topic.getIdentifier(pubQuota, quotaZone, MqttAction.PUBLISH);

		getContext().getQuotaStore(usrConfig.getManagedScope()).put(subQuotaTopic, subQuota, true);
		getContext().getQuotaStore(usrConfig.getManagedScope()).put(pubQuotaTopic, pubQuota, true);
		getServices().getMqtt().publish(subQuotaTopic, getServices().getJsonParser().serialize(subQuota));
		getServices().getMqtt().publish(pubQuotaTopic, getServices().getJsonParser().serialize(pubQuota));
	}

	public void injectFceEnvironment(FceContext context, FceServiceFactory services) {
		this.context = context;
		this.services = services;
	}

	
	public abstract boolean canDoOperation(AuthorizationProperties properties, MqttAction operation);
}
