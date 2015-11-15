package org.eclipse.moquette.fce.event;

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

	FceServiceFactory services;

	public MqttEventHandler(FceServiceFactory services) {
		this.services = services;
	}

	@Override
	public void connectionLost(Throwable arg0) {
		try {
			services.getMqttDataStore().connect();
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

		switch (topicZone) {
		case MANAGED_INTENT:
		case MANAGED_CONFIGURATION:
			UserConfiguration msgConfig = services.getJsonParser().deserializeUserConfiguration(msgPayload);
			services.getConfigDbService().put(topicIdentifier, msgConfig);
			break;
		case MANAGED_QUOTA:
			Quota msgQuota = services.getJsonParser().deserializeQuota(msgPayload);
			services.getQuotaDbService().put(topicIdentifier, msgQuota);
			break;
		default:
			break;
		}

	}

}
