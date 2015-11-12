package org.eclipse.moquette.fce.event;

import org.eclipse.moquette.fce.service.MqttDataStoreService;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class FceMqttEventHandler implements MqttCallback{

	MqttDataStoreService mqttDataStore;
	
	public FceMqttEventHandler(MqttDataStoreService mqttDataStore) {
		this.mqttDataStore = mqttDataStore;
	}

	@Override
	public void connectionLost(Throwable arg0) {
		try {
			mqttDataStore.connect();
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
	public void messageArrived(String arg0, MqttMessage arg1) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
