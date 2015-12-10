package io.moquette.fce.tools;

import javax.net.ssl.SSLSocketFactory;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

public class ShowcaseInvalidLogin extends Showcase {

	public static void main(String[] args) throws Exception {
		// should not work
		tryToLoginWithInvalidAuthentication();
	}

	public static void tryToLoginWithInvalidAuthentication() throws Exception {
		MqttClient client;
		client = new MqttClient("ssl://localhost:8883", "test");

		SSLSocketFactory ssf = configureSSLSocketFactory();

		MqttConnectOptions options = new MqttConnectOptions();
		options.setUserName("hello");
		options.setPassword("hello".toCharArray());
		options.setSocketFactory(ssf);

		client.connect(options);
		client.setTimeToWait(1000);
		client.publish("/test1", "test".getBytes(), Showcase.FIRE_AND_FORGET, false);
		client.disconnect();
		client.close();
	}
}
