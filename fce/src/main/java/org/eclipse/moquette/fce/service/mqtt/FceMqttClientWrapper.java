package org.eclipse.moquette.fce.service.mqtt;

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

import org.eclipse.moquette.fce.context.FceContext;
import org.eclipse.moquette.fce.event.internal.IFceMqttCallback;
import org.eclipse.moquette.fce.event.internal.MqttManageHandler;
import org.eclipse.moquette.fce.exception.FceSystemException;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
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
public class FceMqttClientWrapper implements MqttService{

	private static final Logger log = Logger.getLogger(FceMqttClientWrapper.class.getName());
	
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
		String ssl_port = context.getPluginConfig().getProperty("ssl_port");
		log.info("try to connect to ssl://localhost:" + ssl_port);

		try {
			client = new MqttAsyncClient("ssl://localhost:" + ssl_port, context.getPluginIdentifier());

			SSLSocketFactory ssf = configureSSLSocketFactory();

			options.setUserName(context.getPluginUser());
			options.setPassword(context.getPluginPw().toCharArray());
			options.setSocketFactory(ssf);
			client.setCallback(callback);
			
			client.connect(options, null, new IMqttActionListener() {
				@Override
				public void onSuccess(IMqttToken asyncActionToken) {
					for(String subscriptionTopic : initalizationSubscriptions){
					addNewSubscription(subscriptionTopic);
					setInitialized();
					log.info("client connected");
					}
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

	public boolean isInitialized() {
		return this.initialized;
	}
	
	public IFceMqttCallback getCallback(String topic){
		return subscribedTopics.getOrDefault(topic, new MqttManageHandler());
	}
	
	public void addNewSubscription(String topic) {
		addNewSubscription(topic, null);
	}
	
	public void addNewSubscription(String topic, IFceMqttCallback handler) {
		try {
			subscribedTopics.put(topic, handler);
			client.subscribe(topic, OOS_AT_LEAST_ONCE, null, new IMqttActionListener() {
				@Override
				public void onSuccess(IMqttToken asyncActionToken) {
					setInitialized();
					log.info("topics registered,client initialized");
				}

				@Override
				public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
					log.log(Level.INFO, "user subscription could not be registered", exception);
				}
			});
		} catch (MqttException e) {
			log.log(Level.INFO, "user subscription could not be registered", e);
		}
	}

	public void unregisterSubscriptions() throws MqttException {
		client.unsubscribe(subscribedTopics.keySet().toArray(new String[subscribedTopics.size()]), null,
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
		subscribedTopics.clear();
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
		client.setCallback(callback);
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
	private SSLSocketFactory configureSSLSocketFactory() throws KeyManagementException, NoSuchAlgorithmException,
			UnrecoverableKeyException, IOException, CertificateException, KeyStoreException {
		KeyStore ks = KeyStore.getInstance("JKS");

		log.info("configure PROPS_PLUGIN_JKS_PATH" + context.getPluginJksPath());

		InputStream jksInputStream = jksDatastore(context.getPluginJksPath());
		ks.load(jksInputStream, context.getPluginKeyStorePassword().toCharArray());

		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(ks, context.getPluginKeyManagerPassword().toCharArray());

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
	
	private void setInitialized() {
		this.initialized = true;
	}
}
