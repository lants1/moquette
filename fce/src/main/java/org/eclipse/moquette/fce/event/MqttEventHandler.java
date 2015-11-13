package org.eclipse.moquette.fce.event;

import org.eclipse.moquette.fce.common.ManagedZone;
import org.eclipse.moquette.fce.service.FceServiceFactory;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttEventHandler implements MqttCallback{

	FceServiceFactory services;
	
	public MqttEventHandler(FceServiceFactory services) {
		this.services = services;
	}

	@Override
	public void connectionLost(Throwable arg0) {
		try {
			services.getMqttDataStore().connect();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		if(topic.startsWith(ManagedZone.MANAGED_INTENT.getTopicPrefix())){
			
		}
		
	}

}
