package org.eclipse.moquette.fce.event;

import java.util.logging.Logger;

import org.eclipse.moquette.fce.common.ManagedTopicIdentifierUtil;
import org.eclipse.moquette.fce.common.ManagedZone;
import org.eclipse.moquette.fce.common.ManagedZoneUtil;
import org.eclipse.moquette.fce.model.configuration.UserConfiguration;
import org.eclipse.moquette.fce.model.quota.Quota;
import org.eclipse.moquette.fce.service.FceServiceFactory;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttEventHandler implements MqttCallback {

	private final static Logger log = Logger.getLogger(MqttEventHandler.class.getName());
	
	FceServiceFactory services;

	public MqttEventHandler(FceServiceFactory services) {
		this.services = services;
	}

	@Override
	public void connectionLost(Throwable arg0) {
		try {
			log.warning("internal plugin mqttclient conection to broker connection lost");
			services.getMqttService().connect();
		} catch (MqttException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// doNothing
	}

	@Override
	public void messageArrived(String topicIdentifier, MqttMessage message) throws Exception {
		ManagedZone topicZone = ManagedZoneUtil.getZoneForTopic(topicIdentifier);
		String msgPayload = String.valueOf(message.getPayload());
		String userTopic;
		
		switch (topicZone) {
		case MANAGED_INTENT:
		case MANAGED_CONFIGURATION:
			// less safety, login and hash needs to be sent in the same message
			// direct access to the broker would be a safer solution
			// in this case there would be no need to transmit the hash and login information in the same message
			// TODO Lan maybe implement another handler for internal broker actions
			UserConfiguration msgConfig = services.getJsonParser().deserializeUserConfiguration(msgPayload);
			userTopic = ManagedTopicIdentifierUtil.addUserSpecificTopicFilter(topicIdentifier, msgConfig);
			services.getConfigDbService().put(userTopic, msgConfig);
			services.getMqttService().publish(userTopic, msgPayload, true);
			log.fine("received configuration message for topic: " + topicIdentifier);
			break;
		case MANAGED_QUOTA:
			// TODO Lan generic implementation....
			Quota msgQuota = services.getJsonParser().deserializeQuota(msgPayload);
			userTopic = ManagedTopicIdentifierUtil.addUserSpecificTopicFilter(topicIdentifier, msgQuota);
			services.getQuotaDbService().put(topicIdentifier, msgQuota);
			services.getMqttService().publish(userTopic, msgPayload, true);
			log.fine("received quota message for topic: " + topicIdentifier);
			break;
		default:
			break;
		}

	}

}
