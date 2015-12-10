package io.moquette.fce.tools;

import javax.net.ssl.SSLSocketFactory;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import io.moquette.fce.tools.callback.SampleFceClientCallback;

public class ShowcaseAnonymousLogin extends Showcase {

	public static void main(String[] args) throws Exception {
		loginAnonymouslyAndPublishToTopic();

	}

	public static void loginAnonymouslyAndPublishToTopic() throws Exception {

		MqttClient client;
		client = new MqttClient("ssl://localhost:8883", "test");

		SSLSocketFactory ssf = configureSSLSocketFactory();

		MqttConnectOptions options = new MqttConnectOptions();
		options.setSocketFactory(ssf);

		client.connect(options);
		client.setCallback(new SampleFceClientCallback("anonymous"));
		client.subscribe("/test1");
		client.publish("/test1", "test".getBytes(), Showcase.FIRE_AND_FORGET, false);
		Thread.sleep(3000);
		client.disconnect();
		client.close();

	}

}
