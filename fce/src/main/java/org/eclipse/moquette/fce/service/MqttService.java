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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.eclipse.moquette.fce.FcePlugin;
import org.eclipse.moquette.fce.event.MqttEventHandler;
import org.eclipse.moquette.fce.exception.FceSystemException;
import org.eclipse.moquette.fce.model.common.ManagedZone;
import org.eclipse.moquette.plugin.IBrokerConfig;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

/**
 * Internal Plugin MqttClient which connects to the broker. Provides method for
 * publishing retained messages to store plugin information on the broker.
 * 
 * @author lants1
 *
 */
public class MqttService {

	private static final int OOS_AT_LEAST_ONCE = 1;
	private static final Logger log = Logger.getLogger(MqttService.class.getName());

	private MqttAsyncClient client;
	private IBrokerConfig config;
	private MqttEventHandler eventHandler;
	private MqttConnectOptions options = new MqttConnectOptions();
	private List<String> registeredTopics = new ArrayList<>();
	boolean initialized = false;

	public MqttService(IBrokerConfig config, MqttEventHandler eventHandler) {
		this.config = config;
		this.eventHandler = eventHandler;
	}

	public void initializeInternalMqttClient() {
		String ssl_port = config.getProperty("ssl_port");
		log.info("try to connect to ssl://localhost:" + ssl_port);

		registeredTopics.add(ManagedZone.QUOTA_GLOBAL.getTopicFilter());
		registeredTopics.add(ManagedZone.CONFIG_GLOBAL.getTopicFilter());
		registeredTopics.add(ManagedZone.QUOTA_PRIVATE.getTopicFilter());
		registeredTopics.add(ManagedZone.CONFIG_PRIVATE.getTopicFilter());

		try {
			client = new MqttAsyncClient("ssl://localhost:" + ssl_port,
					config.getProperty(FcePlugin.PROPS_PLUGIN_CLIENT_IDENTIFIER));

			SSLSocketFactory ssf = configureSSLSocketFactory(config);

			options.setUserName(config.getProperty(FcePlugin.PROPS_PLUGIN_CLIENT_USERNAME));
			options.setPassword(config.getProperty(FcePlugin.PROPS_PLUGIN_CLIENT_PASSWORD).toCharArray());
			options.setSocketFactory(ssf);

			client.setCallback(eventHandler);
			client.connect(options, null, new IMqttActionListener() {
				@Override
				public void onSuccess(IMqttToken asyncActionToken) {
					try {
						registerSubscriptions();
					} catch (MqttException e) {
						log.log(Level.SEVERE, "subscriptions could not be registered, plugin excecution failed", e);
						throw new FceSystemException(e);
					}
					log.info("client connected");
				}

				@Override
				public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
					log.log(Level.WARNING, "internal mqtt client can't connect to broker", exception);
				}
			});

		} catch (Exception e) {
			throw new FceSystemException(e);
		}
	}

	private void setInitialized() {
		this.initialized = true;
	}

	public boolean isInitialized() {
		return this.initialized;
	}
	
	public boolean isConnected(){
		return client.isConnected();
	}

	public void registerSubscriptions() throws MqttException {
		client.subscribe(registeredTopics.toArray(new String[registeredTopics.size()]),
				new int[] { OOS_AT_LEAST_ONCE, OOS_AT_LEAST_ONCE, OOS_AT_LEAST_ONCE, OOS_AT_LEAST_ONCE }, null,
				new IMqttActionListener() {
					@Override
					public void onSuccess(IMqttToken asyncActionToken) {
						setInitialized();
						log.info("topics registered,client initialized");
					}

					@Override
					public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
						log.log(Level.SEVERE, "subscriptions could not be registered, plugin excecution failed",
								exception);
						throw new FceSystemException("plugin could not subscribe to management topics");
					}
				});
	}

	public void unregisterSubscriptions() throws MqttException {
		client.unsubscribe(registeredTopics.toArray(new String[registeredTopics.size()]), null,
				new IMqttActionListener() {
					@Override
					public void onSuccess(IMqttToken asyncActionToken) {
						try {
							client.disconnect();
						} catch (MqttException e) {
							log.warning("internal client could not disconnect...");
						}
						log.info("topics unregistered,client disconnected");
					}

					@Override
					public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
						log.log(Level.WARNING, "could not unregister subscriptions", exception);
					}
				});
	}

	public void publish(String topic, String json) {
		MqttMessage message = new MqttMessage();
		message.setPayload(json.getBytes());
		message.setRetained(true);
		message.setQos(OOS_AT_LEAST_ONCE);
		try {
			client.publish(topic, message);
			log.info("mqtt message for topic: " + topic + " with content: " + json + " published to internal broker");
		} catch (MqttException e) {
			log.log(Level.WARNING, "mqtt message for topic: " + topic + " with content: " + json
					+ " could not published to internal broker", e);
		}
	}

	public void delete(String topic) {
		MqttMessage message = new MqttMessage();
		message.setPayload(new byte[0]);
		message.setRetained(true);
		message.setQos(OOS_AT_LEAST_ONCE);
		try {
			client.publish(topic, message);
			log.info("topic: " + topic + " deleted.");
		} catch (MqttException e) {
			log.log(Level.WARNING, "delete retained message on topic: " + topic + " failed", e);
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

		log.info("configure PROPS_PLUGIN_JKS_PATH" + config.getProperty(FcePlugin.PROPS_PLUGIN_JKS_PATH));

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
