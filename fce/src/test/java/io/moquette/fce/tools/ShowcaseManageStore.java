package io.moquette.fce.tools;

import javax.net.ssl.SSLSocketFactory;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import io.moquette.fce.model.common.ManagedZone;
import io.moquette.fce.service.FceServiceFactory;
import io.moquette.fce.tools.callback.SampleFceClientCallback;

public class ShowcaseManageStore extends Showcase{

	private static String USERNAME = "user";

	public static void main(String[] args) throws Exception {
		publishToManageStore();
	}

	public static void publishToManageStore() throws Exception {
		MqttClient client;
		client = new MqttClient("ssl://localhost:8883", "client");

		SSLSocketFactory ssf = configureSSLSocketFactory();
		FceServiceFactory services = new FceServiceFactory(null, null);
		
		MqttConnectOptions options = new MqttConnectOptions();
		options.setUserName(USERNAME);
		options.setPassword(services.getHashing().generateHash(USERNAME).toCharArray());
		options.setSocketFactory(ssf);

		client.connect(options);
		client.setTimeToWait(1000);
		client.setCallback(new SampleFceClientCallback());
		
		client.publish(ManagedZone.CONFIG_GLOBAL.getTopicPrefix()+"/test1", "asfd".getBytes(), Showcase.FIRE_AND_FORGET, true);
		client.disconnect();
		client.close();
	}

}
