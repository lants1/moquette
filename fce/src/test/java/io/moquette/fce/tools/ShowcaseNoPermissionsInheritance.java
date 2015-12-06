package io.moquette.fce.tools;

import javax.net.ssl.SSLSocketFactory;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import io.moquette.fce.common.ReadFileUtil;
import io.moquette.fce.model.common.ManagedZone;
import io.moquette.fce.service.FceServiceFactory;
import io.moquette.fce.tools.callback.SampleFceClientCallback;

public class ShowcaseNoPermissionsInheritance extends Showcase{

	private static final String SUBTOPIC = "/test4/bla";
	private static final String TOPIC = "/test4";
	private static String USERNAME_WRONG = "dfasfsdf";

	public static void main(String[] args) throws Exception {
		initializeInternalMqttClient(USERNAME_WRONG);
		Thread.sleep(2000);
		bookTopicQuota(USERNAME_WRONG);
		Thread.sleep(2000);
		bookSubtopicQuota(USERNAME_WRONG);
		Thread.sleep(2000);
		bookTopicQuota(USERNAME_WRONG);
		Thread.sleep(2000);
		bookSubtopicQuota(USERNAME_WRONG); // quota depleted*/
		Thread.sleep(2000);
		bookTopicQuota(USERNAME_WRONG); //  quota depleted*/
	}

	public static void initializeInternalMqttClient(String user) throws Exception {
		MqttClient client;
		client = new MqttClient("ssl://localhost:8883", "clientid"+user);

		SSLSocketFactory ssf = configureSSLSocketFactory();
		FceServiceFactory services = new FceServiceFactory(null, null);
		
		MqttConnectOptions options = new MqttConnectOptions();
		options.setUserName(user);
		System.out.println(services.getHashing().generateHash(user).toCharArray());
		options.setPassword(services.getHashing().generateHash(user).toCharArray());
		options.setSocketFactory(ssf);

		client.connect(options);
		client.setCallback(new SampleFceClientCallback());
		
		String inputJson = ReadFileUtil.readFileString("/fce/showcase_manage.json");
		
		System.out.println("send intent");
		client.publish(ManagedZone.INTENT.getTopicPrefix()+TOPIC, inputJson.getBytes(), Showcase.FIRE_AND_FORGET, true);
		client.disconnect();
		client.close();
	}

	public static void bookTopicQuota(String user) throws Exception {

		MqttClient client;
		client = new MqttClient("ssl://localhost:8883", "clientid"+user);

		SSLSocketFactory ssf = configureSSLSocketFactory();
		FceServiceFactory services = new FceServiceFactory(null, null);
		
		MqttConnectOptions options = new MqttConnectOptions();
		options.setUserName(user);
		options.setPassword(services.getHashing().generateHash(user).toCharArray());
		options.setSocketFactory(ssf);

		client.connect(options);
		client.setCallback(new SampleFceClientCallback());
		System.out.println("send msg");
		client.publish(TOPIC, "test".getBytes(), Showcase.FIRE_AND_FORGET, true);
		client.disconnect();
		client.close();
	}
	
	public static void bookSubtopicQuota(String user) throws Exception {

		MqttClient client;
		client = new MqttClient("ssl://localhost:8883", "clientid"+user);

		SSLSocketFactory ssf = configureSSLSocketFactory();
		FceServiceFactory services = new FceServiceFactory(null, null);
		
		MqttConnectOptions options = new MqttConnectOptions();
		options.setUserName(user);
		options.setPassword(services.getHashing().generateHash(user).toCharArray());
		options.setSocketFactory(ssf);

		client.connect(options);
		client.setCallback(new SampleFceClientCallback());
		System.out.println("send msg");
		client.publish(SUBTOPIC, "test".getBytes(), Showcase.FIRE_AND_FORGET, true);
		client.disconnect();
		client.close();
	}

}
