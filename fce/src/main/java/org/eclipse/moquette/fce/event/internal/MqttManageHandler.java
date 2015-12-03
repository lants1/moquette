package org.eclipse.moquette.fce.event.internal;

import java.util.logging.Logger;

import org.eclipse.moquette.fce.common.util.ManagedZoneUtil;
import org.eclipse.moquette.fce.context.FceContext;
import org.eclipse.moquette.fce.event.FceEventHandler;
import org.eclipse.moquette.fce.exception.FceSystemException;
import org.eclipse.moquette.fce.model.common.ManagedTopic;
import org.eclipse.moquette.fce.model.common.ManagedZone;
import org.eclipse.moquette.fce.model.configuration.UserConfiguration;
import org.eclipse.moquette.fce.model.quota.UserQuota;
import org.eclipse.moquette.fce.service.FceServiceFactory;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.MqttAction;
import org.eclipse.paho.client.mqttv3.MqttMessage;

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