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

import io.moquette.fce.tools.callback.SampleFceClientCallback;

public class ShowcaseAnonymousLogin extends Showcase {

	public static void main(String[] args) throws KeyManagementException, UnrecoverableKeyException,
			NoSuchAlgorithmException, CertificateException, KeyStoreException, MqttException, IOException {
		loginAnonymouslyAndPublishToTopic();

	}

	public static void loginAnonymouslyAndPublishToTopic() throws MqttException, KeyManagementException,
			UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException {

		client1 = new MqttClient("ssl://localhost:8883", "test");

		SSLSocketFactory ssf = configureSSLSocketFactory();

		MqttConnectOptions options = new MqttConnectOptions();
		options.setSocketFactory(ssf);

		client1.connect(options);
		client1.setCallback(new SampleFceClientCallback("anonymous"));
		client1.setTimeToWait(1000);
		client1.publish("/test1", "test".getBytes(), Showcase.FIRE_AND_FORGET, false);
		
		disconnectClients();
	}

}
