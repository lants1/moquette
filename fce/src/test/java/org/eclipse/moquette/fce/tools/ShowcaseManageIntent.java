package org.eclipse.moquette.fce.tools;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.net.ssl.SSLSocketFactory;

import org.eclipse.moquette.fce.common.FceHashUtil;
import org.eclipse.moquette.fce.common.ManagedZone;
import org.eclipse.moquette.fce.tools.callback.SampleFceClientCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

public class ShowcaseManageIntent extends Showcase{

	private static String USERNAME = "user";
	private static String PASSWORD = "password";

	public static void main(String[] args) throws Exception {
		initializeInternalMqttClient();
		Thread.sleep(2000);
		bookQuota();
	}

	public static void initializeInternalMqttClient() throws Exception {
		MqttClient client;
		client = new MqttClient("ssl://localhost:8883", FceHashUtil.getFceHash(USERNAME, PASSWORD));

		SSLSocketFactory ssf = configureSSLSocketFactory();

		MqttConnectOptions options = new MqttConnectOptions();
		options.setUserName(USERNAME);
		options.setPassword(PASSWORD.toCharArray());
		options.setSocketFactory(ssf);

		client.connect(options);
		client.setCallback(new SampleFceClientCallback());
		
		String inputJson = readFile("/showcase_manage.json");
		
		System.out.println("send intent");
		client.publish(ManagedZone.INTENT.getTopicPrefix()+"/test2", inputJson.getBytes(), 1, true);
		client.disconnect();
		client.close();
	}

	public static void bookQuota() throws Exception {

		MqttClient client;
		client = new MqttClient("ssl://localhost:8883", FceHashUtil.getFceHash(USERNAME, PASSWORD));

		SSLSocketFactory ssf = configureSSLSocketFactory();

		MqttConnectOptions options = new MqttConnectOptions();
		options.setUserName(USERNAME);
		options.setPassword(PASSWORD.toCharArray());
		options.setSocketFactory(ssf);

		client.connect(options);
		client.setCallback(new SampleFceClientCallback());
		
		System.out.println("send msg");
		client.publish("/test2", "test".getBytes(), 1, true);
		client.disconnect();
		client.close();
	}
	
	private static String readFile(String path) throws IOException, URISyntaxException {
		byte[] encoded = Files.readAllBytes(Paths.get(ShowcaseManageIntent.class.getResource(path).toURI()));
		return new String(encoded, StandardCharsets.UTF_8);
	}

}
