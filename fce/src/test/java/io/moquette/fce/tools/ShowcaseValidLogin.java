package io.moquette.fce.tools;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLSocketFactory;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import io.moquette.fce.service.FceServiceFactory;
import io.moquette.fce.tools.callback.SampleFceClientCallback;

public class ShowcaseValidLogin extends Showcase{

	private static String USERNAME = "user";
	
	public static void main(String[] args) throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException, KeyStoreException, MqttException, IOException {
		loginAndPublishToTopic();
		
	}

	public static void loginAndPublishToTopic() throws MqttException, KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException {
			
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
			client.setTimeToWait(1000);
			client.publish("/test1", "test".getBytes(), Showcase.FIRE_AND_FORGET, false);
			client.disconnect();
			client.close();
	}

}
