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
import java.util.logging.Logger;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.eclipse.moquette.fce.FcePlugin;
import org.eclipse.moquette.fce.common.ManagedZone;
import org.eclipse.moquette.fce.event.MqttEventHandler;
import org.eclipse.moquette.fce.exception.FceSystemException;
import org.eclipse.moquette.plugin.IBrokerConfig;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

public class MqttService {

	private final static Logger log = Logger.getLogger(MqttService.class.getName());

	MqttClient client;
	IBrokerConfig config;
	MqttEventHandler eventHandler;
	MqttConnectOptions options = new MqttConnectOptions();

	public MqttService(IBrokerConfig config, MqttEventHandler eventHandler) {
		this.config = config;
		this.eventHandler = eventHandler;
	}

	public void initializeInternalMqttClient() {
		String ssl_port = config.getProperty("ssl_port");
		log.info("try to connect to ssl://localhost:" + ssl_port);
		try {
			client = new MqttClient("ssl://localhost:" + ssl_port,
					config.getProperty(FcePlugin.PROPS_PLUGIN_CLIENT_IDENTIFIER));

			SSLSocketFactory ssf = configureSSLSocketFactory(config);

			options.setUserName(config.getProperty(FcePlugin.PROPS_PLUGIN_CLIENT_USERNAME));
			options.setPassword(config.getProperty(FcePlugin.PROPS_PLUGIN_CLIENT_PASSWORD).toCharArray());
			options.setSocketFactory(ssf);

			client.setCallback(eventHandler);
			client.connect(options);
			registerTopics();
			log.info("internal mqtt client connected to broker");

		} catch (Exception e) {
			throw new FceSystemException(e);
		}
	}

	public void registerTopics(){
		subscribe(ManagedZone.INTENT.getTopicFilter());
		subscribe(ManagedZone.QUOTA_GLOBAL.getTopicFilter());
		subscribe(ManagedZone.CONFIG_GLOBAL.getTopicFilter());
		subscribe(ManagedZone.QUOTA_PRIVATE.getTopicFilter());
		subscribe(ManagedZone.CONFIG_PRIVATE.getTopicFilter());
	}
	
	public void publish(String topic, String json) {
		MqttMessage message = new MqttMessage();
		message.setPayload(json.getBytes());
		message.setRetained(true);
		message.setQos(1);
		try {
			client.publish(topic, message);
			log.fine("mqtt message for topic: " + topic + " with content: " + json + " published to internal broker");
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}
	
	public void delete(String topic) {
		MqttMessage message = new MqttMessage();
		message.setPayload(new byte[0]);
		message.setRetained(true);
		try {
			client.publish(topic, message);
			log.fine("topic: " + topic + " deleted.");
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	public void subscribe(String topicFilter) {
		try {
			client.subscribe(topicFilter);
			log.info("internal mqtt client subscribed for: " + topicFilter);
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
		client.setCallback(eventHandler);
		client.connect(options);
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
	private SSLSocketFactory configureSSLSocketFactory(IBrokerConfig config) throws KeyManagementException,
			NoSuchAlgorithmException, UnrecoverableKeyException, IOException, CertificateException, KeyStoreException {
		KeyStore ks = KeyStore.getInstance("JKS");

		log.info("configure PROPS_PLUGIN_JKS_PATH"+config.getProperty(FcePlugin.PROPS_PLUGIN_JKS_PATH));
	
		InputStream jksInputStream = jksDatastore(config.getProperty(FcePlugin.PROPS_PLUGIN_JKS_PATH));
		ks.load(jksInputStream, config.getProperty(FcePlugin.PROPS_PLUGIN_KEY_STORE_PASSWORD).toCharArray());

		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(ks, config.getProperty(FcePlugin.PROPS_PLUGIN_KEY_MANAGER_PASSWORD).toCharArray());

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

		throw new FceSystemException("JKS is not found on path:" + jksPath);
	}
}
