package io.moquette.fce.integration;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.moquette.commons.Constants;
import io.moquette.fce.context.FceContext;
import io.moquette.fce.service.FceServiceFactory;
import io.moquette.server.Server;

public abstract class FceIntegrationTest {

	protected static final String SAMPLE_MESSAGE = "message";

	protected static String USERNAME = "user";
	protected static String OTHER_USERNAME = "otheruser";
	protected static final Logger LOG = LoggerFactory.getLogger(FceEnhancedServerIntegrationTest.class);

	protected FceServiceFactory services;

	protected Server m_server;
	protected static MqttClientPersistence s_dataStore;

	protected IMqttClient m_client;
	protected FceTestCallback m_callback;
	
	@BeforeClass
	public static void beforeTests() {
		String dbPath = FceIntegrationUtils.localMapDBPath();
		File dbFile = new File(dbPath);
		if (dbFile.exists()) {
			dbFile.delete();
		}
		assertFalse(dbFile.exists());
		
		String tmpDir = System.getProperty("java.io.tmpdir");
		s_dataStore = new MqttDefaultFilePersistence(tmpDir);
	}

	protected void startServer() throws IOException {
		String file = getClass().getResource("/").getPath();
		System.setProperty("moquette.path", file);
		m_server = new Server();

		Properties sslProps = new Properties();
		sslProps.put(io.moquette.commons.Constants.SSL_PORT_PROPERTY_NAME, "8883");
		sslProps.put(Constants.JKS_PATH_PROPERTY_NAME, "serverkeystore.jks");
		sslProps.put(Constants.KEY_STORE_PASSWORD_PROPERTY_NAME, "passw0rdsrv");
		sslProps.put(Constants.KEY_MANAGER_PASSWORD_PROPERTY_NAME, "passw0rdsrv");

		sslProps.put(FceContext.PROP_JKS_PATH, "pluginkeystore.jks");
		sslProps.put(FceContext.PROP_KEY_STORE_PASSWORD, "passw0rd");
		sslProps.put(FceContext.PROP_KEY_MANAGER_PASSWORD, "passw0rd");

		sslProps.put(Constants.PERSISTENT_STORE_PROPERTY_NAME, FceIntegrationUtils.localMapDBPath());
		m_server.startServer(sslProps);

		this.services = new FceServiceFactory(null, null);
	}

	@Before
	public void setUp() throws Exception {
		String dbPath = FceIntegrationUtils.localMapDBPath();
		File dbFile = new File(dbPath);
		assertFalse(String.format("The DB storagefile %s already exists", dbPath), dbFile.exists());

		startServer();

		m_client = new MqttClient("ssl://localhost:8883", "TestClient", s_dataStore);
		// m_client = new MqttClient("ssl://test.mosquitto.org:8883",
		// "TestClient", s_dataStore);

		m_callback = new FceTestCallback();
		m_client.setCallback(m_callback);
	}

	@After
	public void tearDown() throws Exception {
		if (m_client != null && m_client.isConnected()) {
			m_client.disconnect();
		}

		if (m_server != null) {
			m_server.stopServer();
		}
		String dbPath = FceIntegrationUtils.localMapDBPath();
		File dbFile = new File(dbPath);
		if (dbFile.exists()) {
			dbFile.delete();
		}
		assertFalse(dbFile.exists());
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
	protected SSLSocketFactory configureSSLSocketFactory() throws KeyManagementException, NoSuchAlgorithmException,
			UnrecoverableKeyException, IOException, CertificateException, KeyStoreException {
		KeyStore ks = KeyStore.getInstance("JKS");
		InputStream jksInputStream = getClass().getClassLoader().getResourceAsStream("pluginkeystore.jks");
		ks.load(jksInputStream, "passw0rd".toCharArray());

		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(ks, "passw0rd".toCharArray());

		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(ks);

		SSLContext sc = SSLContext.getInstance("TLS");
		TrustManager[] trustManagers = tmf.getTrustManagers();
		sc.init(kmf.getKeyManagers(), trustManagers, null);

		SSLSocketFactory ssf = sc.getSocketFactory();
		return ssf;
	}
}
