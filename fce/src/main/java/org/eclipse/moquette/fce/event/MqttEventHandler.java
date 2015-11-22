package org.eclipse.moquette.fce.event;

import java.util.logging.Logger;

import org.eclipse.moquette.fce.common.ManagedZone;
import org.eclipse.moquette.fce.common.ManagedZoneUtil;
import org.eclipse.moquette.fce.common.converter.QuotaConverter;
import org.eclipse.moquette.fce.exception.FceSystemException;
import org.eclipse.moquette.fce.model.ManagedTopic;
import org.eclipse.moquette.fce.model.configuration.UserConfiguration;
import org.eclipse.moquette.fce.model.quota.UserQuota;
import org.eclipse.moquette.fce.service.IFceServiceFactory;
import org.eclipse.moquette.plugin.MqttAction;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttEventHandler implements MqttCallback {

	private final static Logger log = Logger.getLogger(MqttEventHandler.class.getName());

	IFceServiceFactory services;

	public MqttEventHandler(IFceServiceFactory services) {
		this.services = services;
	}

	@Override
	public void connectionLost(Throwable arg0) {
		try {
			log.warning("internal plugin mqttclient conection to broker connection lost");
			services.getMqtt().connect();
		} catch (MqttException e) {
			throw new FceSystemException(e);
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// doNothing
	}

	// TODO lants1 better implementation
	@Override
	public void messageArrived(String topicIdentifier, MqttMessage message) throws Exception {
		ManagedZone zone = ManagedZoneUtil.getZoneForTopic(topicIdentifier);
		String msgPayload = String.valueOf(message.getPayload());
		ManagedTopic topic = new ManagedTopic(topicIdentifier);

		switch (zone) {
		case INTENT:
			UserConfiguration usrConfig = services.getJsonParser().deserializeUserConfiguration(msgPayload);
	
			services.getConfigDb().put(topic.getIdentifier(usrConfig, zone), usrConfig);
			services.getMqtt().publish(topic.getIdentifier(usrConfig, zone), msgPayload);
			
			UserQuota subQuota =  QuotaConverter.convertSubscribeConfiguration(usrConfig);
			UserQuota pubQuota =  QuotaConverter.convertPublishConfiguration(usrConfig);
		
			String subQuotaTopic = topic.getIdentifier(subQuota, ManagedZone.QUOTA, MqttAction.SUBSCRIBE);
			String pubQuotaTopic = topic.getIdentifier(pubQuota, ManagedZone.QUOTA, MqttAction.PUBLISH);
			
			services.getQuotaDb().put(subQuotaTopic, subQuota, true);
			services.getQuotaDb().put(pubQuotaTopic, pubQuota, true);
			services.getMqtt().publish(subQuotaTopic, services.getJsonParser().serialize(subQuota));
			services.getMqtt().publish(pubQuotaTopic, services.getJsonParser().serialize(pubQuota));
			
			log.fine("received configuration message for topic: " + topicIdentifier);
			break;
		case CONFIGURATION:
			if (!services.isInitialized()) {
				UserConfiguration msgConfig = services.getJsonParser().deserializeUserConfiguration(msgPayload);
				services.getConfigDb().put(topic.getIdentifier(msgConfig, zone), msgConfig);
				services.getMqtt().publish(topic.getIdentifier(msgConfig, zone), msgPayload);
				log.fine("received configuration message for topic: " + topicIdentifier);
			}
			break;
		case QUOTA:
			if (!services.isInitialized()) {
				UserQuota msgQuota = services.getJsonParser().deserializeQuota(msgPayload);
				services.getQuotaDb().put(topic.getIdentifier(msgQuota, zone), msgQuota, true);
				services.getMqtt().publish(topic.getIdentifier(msgQuota, zone), msgPayload);
				log.fine("received quota message for topic: " + topicIdentifier);
			}
			break;
		default:
			break;
		}

	}

}
