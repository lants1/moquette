package io.moquette.fce.event.internal;

import java.util.logging.Logger;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import io.moquette.fce.common.util.ManagedZoneUtil;
import io.moquette.fce.context.FceContext;
import io.moquette.fce.event.FceEventHandler;
import io.moquette.fce.exception.FceSystemException;
import io.moquette.fce.model.common.ManagedTopic;
import io.moquette.fce.model.common.ManagedZone;
import io.moquette.fce.model.configuration.UserConfiguration;
import io.moquette.fce.model.quota.UserQuota;
import io.moquette.fce.service.FceServiceFactory;
import io.moquette.plugin.AuthorizationProperties;
import io.moquette.plugin.MqttAction;

public class MqttManageHandler extends FceEventHandler implements IFceMqttCallback {

	private final static Logger log = Logger.getLogger(MqttManageHandler.class.getName());

	public MqttManageHandler(){
		super(null, null);
	}
	
	public MqttManageHandler(FceContext context, FceServiceFactory services) {
		super(context, services);
	}

	@Override
	public void messageArrived(String topicIdentifier, MqttMessage message) throws Exception {
		log.info("received internal message for topic:" + topicIdentifier);
		ManagedZone zone = ManagedZoneUtil.getZoneForTopic(topicIdentifier);
		String msgPayload = message.toString();
		ManagedTopic topic = new ManagedTopic(topicIdentifier);
		switch (zone) {
		case CONFIG_PRIVATE:
		case CONFIG_GLOBAL:
			UserConfiguration msgConfig = getServices().getJsonParser().deserializeUserConfiguration(msgPayload);
			getContext().getConfigurationStore(zone).put(topic.getIdentifier(msgConfig, zone), msgConfig);
			for(String schemaTopic : msgConfig.getSchemaTopics()){
				getServices().getMqtt().addNewSubscription(schemaTopic, new MqttSchemaHandler());
			}
			log.info("received configuration message for topic: " + topicIdentifier);
			break;
		case QUOTA_PRIVATE:
		case QUOTA_GLOBAL:
			UserQuota msgQuota = getServices().getJsonParser().deserializeQuota(msgPayload);
			getContext().getQuotaStore(zone).put(topic.getIdentifier(msgQuota, zone), msgQuota, true);
			log.info("received quota message for topic: " + topicIdentifier);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean canDoOperation(AuthorizationProperties properties, MqttAction operation) {
		throw new FceSystemException("not implemented");
	}
}