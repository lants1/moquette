package org.eclipse.moquette.fce.tools;

import javax.net.ssl.SSLSocketFactory;

import org.eclipse.moquette.fce.common.util.FceHashUtil;
import org.eclipse.moquette.fce.model.common.ManagedZone;
import org.eclipse.moquette.fce.tools.callback.SampleFceClientCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

public class ShowcaseManageStore extends Showcase{

	private static String USERNAME = "user";
	private static String PASSWORD = "password";

	public static void main(String[] args) throws Exception {
		publishToManageStore();
	}

	public static void publishToManageStore() throws Exception {
		MqttClient client;
		client = new MqttClient("ssl://localhost:8883", FceHashUtil.getFceHash(USERNAME, PASSWORD));

		SSLSocketFactory ssf = configureSSLSocketFactory();

		MqttConnectOptions options = new MqttConnectOptions();
		options.setUserName(USERNAME);
		options.setPassword(PASSWORD.toCharArray());
		options.setSocketFactory(ssf);

		client.connect(options);
		client.setCallback(new SampleFceClientCallback());
		
		
		client.publish(ManagedZone.CONFIG_GLOBAL.getTopicPrefix()+"/test1", "asfd".getBytes(), 1, true);
		client.disconnect();
		client.close();
	}

}
