package io.moquette.fce.tools;

import javax.net.ssl.SSLSocketFactory;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import io.moquette.fce.common.ReadFileUtil;
import io.moquette.fce.model.common.ManagedZone;
import io.moquette.fce.service.FceServiceFactory;
import io.moquette.fce.tools.callback.SampleFceClientCallback;

public class ShowcaseManageIntent extends Showcase{

	private static String USERNAME = "user";

	public static void main(String[] args) throws Exception {
		initializeInternalMqttClient();
		Thread.sleep(2000);
		bookQuota();
	}

	public static void initializeInternalMqttClient() throws Exception {
		MqttClient client;
		client = new MqttClient("ssl://localhost:8883", "client");

		SSLSocketFactory ssf = configureSSLSocketFactory();
		FceServiceFactory services = new FceServiceFactory(null, null);
		
		MqttConnectOptions options = new MqttConnectOptions();
		options.setUserName(USERNAME);
		System.out.println(services.getHashing().generateHash(USERNAME).toCharArray());
		options.setPassword(services.getHashing().generateHash(USERNAME).toCharArray());
		options.setSocketFactory(ssf);

		client.connect(options);
		client.setCallback(new SampleFceClientCallback());
		
		String inputJson = ReadFileUtil.readFileString("/fce/showcase_manage.json");
		
		System.out.println("send intent");
		client.publish(ManagedZone.INTENT.getTopicPrefix()+"/test2", inputJson.getBytes(), 1, true);
		client.disconnect();
		client.close();
	}

	public static void bookQuota() throws Exception {

		MqttClient client;
		client = new MqttClient("ssl://localhost:8883", "client");

		SSLSocketFactory ssf = configureSSLSocketFactory();
		FceServiceFactory services = new FceServiceFactory(null, null);
		
		MqttConnectOptions options = new MqttConnectOptions();
		options.setUserName(USERNAME);
		options.setPassword(services.getHashing().generateHash(USERNAME).toCharArray());
		options.setSocketFactory(ssf);

		client.connect(options);
		client.setCallback(new SampleFceClientCallback());
		
		System.out.println("send msg");
		client.publish("/test2", "test".getBytes(), 1, true);
		client.disconnect();
		client.close();
	}

}
