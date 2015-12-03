package org.eclipse.moquette.fce.tools;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLSocketFactory;

import org.eclipse.moquette.fce.service.FceServiceFactory;
import org.eclipse.moquette.fce.tools.callback.SampleFceClientCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

public class ShowcaseValidLogin extends Showcase{

	private static String USERNAME = "user";
	
	public static void main(String[] args) throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException, KeyStoreException, MqttException, IOException {
		loginAndPublishToTopic();
		
	}

	public static void loginAndPublishToTopic() throws MqttException, KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException {
			
		MqttClient client;
		client = new MqttClient("ssl://localhost:8883", "client");

			SSLSocketFactory ssf = configureSSLSocketFactory();
			FceServiceFactory services = new FceServiceFactory(null);
			
			MqttConnectOptions options = new MqttConnectOptions();
			options.setUserName(USERNAME);
			options.setPassword(services.getHashing().generateHash(USERNAME).toCharArray());
			options.setSocketFactory(ssf);

			client.connect(options);
			client.setCallback(new SampleFceClientCallback());
			client.publish("/test1", "test".getBytes(), 0, false);
			client.disconnect();
			client.close();
	}

}
