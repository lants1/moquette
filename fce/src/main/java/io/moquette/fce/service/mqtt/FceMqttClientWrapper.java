package io.moquette.fce.service.mqtt;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import io.moquette.fce.context.FceContext;
import io.moquette.fce.event.internal.IFceMqttCallback;
import io.moquette.fce.event.internal.MqttManageHandler;
import io.moquette.fce.exception.FceSystemException;
import io.moquette.fce.service.mqtt.websocket.MqttWebSocketAsyncClient;

/**
 * Internal Plugin MqttClient which connects to the broker. Provides method for
 * publishing retained messages to store plugin information on the broker.
 * 
 * @author lants1
 *
 */
public class FceMqttClientWrapper implements MqttService {

	private static final Logger LOGGER = Logger.getLogger(FceMqttClientWrapper.class.getName());

	private static final int OOS_AT_LEAST_ONCE = 1;

	private MqttAsyncClient client;
	private MqttCallback callback;
	private MqttConnectOptions options = new MqttConnectOptions();

	private Map<String, IFceMqttCallback> subscribedTopics = new HashMap<>();

	private FceContext context;
	private boolean initialized = false;

	public FceMqttClientWrapper(FceContext context, MqttCallback callback) {
		this.context = context;
		this.callback = callback;
	}

	public void initializeInternalMqttClient(final List<String> initalizationSubscriptions) {
		try {
			client = getMqttClient();

			SSLSocketFactory ssf = configureSSLSocketFactory();

			options.setUserName(context.getPluginUser());
			options.setPassword(context.getPluginPw().toCharArray());
			options.setSocketFactory(ssf);
			options.setCleanSession(false);
			options.setKeepAliveInterval(30);
			client.setCallback(callback);

			client.connect(options, null, new IMqttActionListener() {
				@Override
				public void onSuccess(IMqttToken asyncActionToken) {
					for (String subscriptionTopic : initalizationSubscriptions) {
						addNewSubscription(subscriptionTopic);
						setInitialized();
						LOGGER.info("client connected");
					}
				}

				@Override
				public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
					LOGGER.log(Level.WARNING, "internal mqtt client can't connect to broker", exception);
				}
			});

		} catch (Exception e) {
			throw new FceSystemException(e);
		}
	}

	public boolean isInitialized() {
		return this.initialized;
	}

	public IFceMqttCallback getCallback(String topic) {
		return subscribedTopics.getOrDefault(topic, new MqttManageHandler());
	}

	public void addNewSubscription(String topic) {
		addNewSubscription(topic, null);
	}

	@Override
	public void addNewSubscription(String topic, IFceMqttCallback handler) {
		try {
			subscribedTopics.put(topic, handler);
			client.subscribe(topic, OOS_AT_LEAST_ONCE, null, new IMqttActionListener() {
				@Override
				public void onSuccess(IMqttToken asyncActionToken) {
					setInitialized();
					LOGGER.info("topics registered,client initialized");
				}

				@Override
				public void onFailure(IMqttToken asyncActionToken, Throwable e) {
					throw new FceSystemException("internal client can't connect to broker", e);
				}
			});
		} catch (MqttException e) {
			LOGGER.log(Level.INFO, "user subscription could not be registered", e);
		}
	}

	public void unregisterSubscriptions() throws MqttException {
		client.unsubscribe(subscribedTopics.keySet().toArray(new String[subscribedTopics.size()]));
		subscribedTopics.clear();
	}

	@Override
	public void publish(String topic, String json) {
		publish(topic, json, true);
	}
	
	private void publish(String topic, String json, boolean retained) {
		MqttMessage message = new MqttMessage();
		message.setPayload(json.getBytes());
		message.setRetained(retained);
		message.setQos(OOS_AT_LEAST_ONCE);
		try {
			client.publish(topic, message, null, new IMqttActionListener() {
				@Override
				public void onSuccess(IMqttToken asyncActionToken) {
					LOGGER.info("mqtt message for topic: " + topic + " with content: " + json
							+ " published to internal broker");
				}

				@Override
				public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
					LOGGER.log(Level.WARNING, "publish failed, retry", exception);
				}
			});
		} catch (MqttException e) {
			LOGGER.log(Level.WARNING, "mqtt message for topic: " + topic + " with content: " + json
					+ " could not published to internal broker", e);
		}
	}

	@Override
	public void delete(String topic) {
		MqttMessage message = new MqttMessage();
		message.setPayload(new byte[0]);
		message.setRetained(true);
		message.setQos(OOS_AT_LEAST_ONCE);
		try {
			client.publish(topic, message, null, new IMqttActionListener() {
				@Override
				public void onSuccess(IMqttToken asyncActionToken) {
					LOGGER.info("topic: " + topic + " deleted.");
				}

				@Override
				public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
					LOGGER.log(Level.WARNING, "publish failed, retry", exception);
				}
			});
		} catch (MqttException e) {
			LOGGER.log(Level.WARNING, "delete retained message on topic: " + topic + " failed", e);
		}
	}

	public void connect() throws MqttException {
		client.setCallback(callback);
		client.connect(options, null, new IMqttActionListener() {
			@Override
			public void onSuccess(IMqttToken asyncActionToken) {
				LOGGER.info("client reconnected");
			}

			@Override
			public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
				LOGGER.log(Level.WARNING, "internal mqtt client can't connect to broker", exception);
			}
		});
	}
	
	/**
	 * Disconnects the mqtt client.
	 *
	 * @throws MqttException
	 */
	public void disconnect() throws MqttException{
		client.disconnect();
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
	private SSLSocketFactory configureSSLSocketFactory() throws KeyManagementException, NoSuchAlgorithmException,
			UnrecoverableKeyException, IOException, CertificateException, KeyStoreException {
		KeyStore ks = KeyStore.getInstance("JKS");

		LOGGER.info("configure PROPS_PLUGIN_JKS_PATH" + context.getJksPath());

		InputStream jksInputStream = jksDatastore(context.getJksPath());
		ks.load(jksInputStream, context.getKeyStorePassword().toCharArray());

		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(ks, context.getKeyManagerPassword().toCharArray());

		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(ks);

		SSLContext sc = SSLContext.getInstance("TLS");
		TrustManager[] trustManagers = tmf.getTrustManagers();
		sc.init(kmf.getKeyManagers(), trustManagers, null);

		return sc.getSocketFactory();
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

	private void setInitialized() {
		this.initialized = true;
	}

	private MqttAsyncClient getMqttClient() throws MqttException {
		MemoryPersistence dataStore = new MemoryPersistence();
		String connection;
		if (context.getSecureWebsocketPort().isEmpty()) {
			connection = "ssl://localhost:" + context.getSslPort();
			LOGGER.info("try to connect to connection:" + connection);
			return new MqttAsyncClient(connection, context.getPluginIdentifier(), dataStore);
		}

		connection = "wss://localhost:" + context.getSecureWebsocketPort();
		LOGGER.info("try to connect to connection:" + connection);
		return new MqttWebSocketAsyncClient(connection, context.getPluginIdentifier(), dataStore);
	}

}
