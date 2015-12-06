package io.moquette.fce.tools;

import javax.net.ssl.SSLSocketFactory;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import io.moquette.fce.common.ReadFileUtil;
import io.moquette.fce.model.common.ManagedZone;
import io.moquette.fce.service.FceServiceFactory;
import io.moquette.fce.tools.callback.SampleFceClientCallback;

public class ShowcaseManageXmlSchemaIntent extends Showcase{

	private static final String SCHEMA_TOPIC = "/xml/schema";
	private static final String TOPIC = "/test77";
	private static String USERNAME = "user";

	public static void main(String[] args) throws Exception {
		initializeInternalMqttClient();
		Thread.sleep(2000);
		bookXmlGood();
		Thread.sleep(2000); // accepted
		bookXmlBad();
		Thread.sleep(2000); // rejected
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
		client.setTimeToWait(1000);
		
		String manageFile = ReadFileUtil.readFileString("/fce/showcase_manage_xml.json");
		byte[] schemaXml = ReadFileUtil.readFileBytes("/validation/xml/sampleSchema.xml");
		
		System.out.println("send intent");
		client.publish(SCHEMA_TOPIC, schemaXml, 1, true);
		client.publish(ManagedZone.INTENT.getTopicPrefix()+TOPIC, manageFile.getBytes(), Showcase.FIRE_AND_FORGET, true);
		client.disconnect();
		client.close();
	}

	public static void bookXmlGood() throws Exception {

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
		
		String jsonGood = ReadFileUtil.readFileString("/validation/xml/valid.xml");
		System.out.println("send good msg");
		client.publish(TOPIC, jsonGood.getBytes(), Showcase.FIRE_AND_FORGET, true);
		client.disconnect();
		client.close();
	}
	

	public static void bookXmlBad() throws Exception {

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
		
		String jsonBad = ReadFileUtil.readFileString("/validation/xml/invalid.xml");
		System.out.println("send bad json");
		client.publish(TOPIC, jsonBad.getBytes(), Showcase.FIRE_AND_FORGET, true);
		client.disconnect();
		client.close();
	}

}
