package org.eclipse.moquette.fce.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Properties;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.eclipse.moquette.fce.event.MqttEventHandler;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

public class MqttDataStoreService {

	private static final String PLUGIN_KEY_MANAGER_PASSWORD = "plugin_key_manager_password";
	private static final String PLUGIN_KEY_STORE_PASSWORD = "plugin_key_store_password";
	private static final String PLUGIN_JKS_PATH = "plugin_jks_path";

	MqttClient client;
	Properties config;
	MqttEventHandler eventHandler;

	public MqttDataStoreService(Properties config, MqttEventHandler eventHandler) {
		this.config = config;
		this.eventHandler = eventHandler;
	}

	public void initializeDataStore() {
		String ssl_port = config.getProperty("ssl_port");

		try {
			client = new MqttClient("ssl://localhost:" + ssl_port, "Sending");

			SSLSocketFactory ssf = configureSSLSocketFactory(config);

			MqttConnectOptions options = new MqttConnectOptions();
			options.setUserName("user");
			options.setPassword("pw".toCharArray());
			options.setSocketFactory(ssf);

			client.connect();
			client.setCallback(eventHandler);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void publish(String topic, String json) {
		MqttMessage message = new MqttMessage();
		message.setPayload("A single message from my computer fff".getBytes());
		try {
			client.publish(topic, message);
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}
	
	public void subscribe(String topicFilter) {
		try {
			client.subscribe(topicFilter);
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}
	
	public void unsubscribe(String topicFilter) {
		try {
			client.unsubscribe(topicFilter);
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	public void connect() throws MqttSecurityException, MqttException {
			client.connect();
	}

	/**
	 * keystore generated into test/resources with command:
	 * 
	 * keytool -keystore clientkeystore.jks -alias testclient -genkey -keyalg
	 * RSA -> mandatory to put the name surname -> password is passw0rd -> type
	 * yes at the end
	 * 
	 * to generate the crt file from the keystore -- keytool -certreq -alias
	 * testclient -keystore clientkeystore.jks -file testclient.csr
	 * 
	 * keytool -export -alias testclient -keystore clientkeystore.jks -file
	 * testclient.crt
	 * 
	 * to import an existing certificate: keytool -keystore clientkeystore.jks
	 * -import -alias testclient -file testclient.crt -trustcacerts
	 */
	private SSLSocketFactory configureSSLSocketFactory(Properties config) throws KeyManagementException,
			NoSuchAlgorithmException, UnrecoverableKeyException, IOException, CertificateException, KeyStoreException {
		KeyStore ks = KeyStore.getInstance("JKS");

		InputStream jksInputStream = jksDatastore(config.getProperty(PLUGIN_JKS_PATH));
		ks.load(jksInputStream, config.getProperty(PLUGIN_KEY_STORE_PASSWORD).toCharArray());

		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(ks, config.getProperty(PLUGIN_KEY_MANAGER_PASSWORD).toCharArray());

		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(ks);

		SSLContext sc = SSLContext.getInstance("TLS");
		TrustManager[] trustManagers = tmf.getTrustManagers();
		sc.init(kmf.getKeyManagers(), trustManagers, null);

		SSLSocketFactory ssf = sc.getSocketFactory();
		return ssf;
	}

	private InputStream jksDatastore(String jksPath) throws FileNotFoundException {
		URL jksUrl = getClass().getClassLoader().getResource(jksPath);
		File jksFile = new File(jksPath);
		if (jksFile.exists()) {
			return new FileInputStream(jksFile);
		}

		if (jksUrl != null) {
			return getClass().getClassLoader().getResourceAsStream(jksPath);
		}

		throw new RuntimeException("JKS is not found on path:" + jksPath);
	}
}
