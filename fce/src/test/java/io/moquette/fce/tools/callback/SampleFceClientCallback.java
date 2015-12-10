package io.moquette.fce.tools.callback;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class SampleFceClientCallback implements MqttCallback {

	private String user = "";
	
	public SampleFceClientCallback(String user) {
		super();
		this.user = user;
	}

	@Override
	public void connectionLost(Throwable arg0) {
		// nothing to log
		
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// nothing to log
	}

	@Override
	public void messageArrived(String topic, MqttMessage msg) throws Exception {
		System.out.println("--------------------------------");
		System.out.println("::::::: User:"+ user +" ::::::::");
		System.out.println("topic:" + topic + "payload:" + new String(msg.getPayload(), "UTF-8"));
	}

}
