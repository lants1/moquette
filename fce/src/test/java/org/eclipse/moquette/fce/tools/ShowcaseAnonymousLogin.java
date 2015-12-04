package org.eclipse.moquette.fce.tools;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLSocketFactory;

import org.eclipse.moquette.fce.tools.callback.SampleFceClientCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

public class ShowcaseAnonymousLogin extends Showcase {

	public static void main(String[] args) throws KeyManagementException, UnrecoverableKeyException,
			NoSuchAlgorithmException, CertificateException, KeyStoreException, MqttException, IOException {
		loginAnonymouslyAndPublishToTopic();

	}

	public static void loginAnonymouslyAndPublishToTopic() throws MqttException, KeyManagementException,
			UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException {

		MqttClient client;
		client = new MqttClient("ssl://localhost:8883", "test");

		SSLSocketFactory ssf = configureSSLSocketFactory();

		MqttConnectOptions options = new MqttConnectOptions();
		options.setSocketFactory(ssf);

		client.connect(options);
		client.setCallback(new SampleFceClientCallback());
		client.publish("/test1", "test".getBytes(), 0, false);
		client.disconnect();
		client.close();

	}

}
