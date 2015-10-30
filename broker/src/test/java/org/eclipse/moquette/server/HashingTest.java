package org.eclipse.moquette.server;

import org.eclipse.moquette.server.config.IConfig;
import org.eclipse.moquette.server.config.MemoryConfig;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.eclipse.moquette.commons.Constants.PERSISTENT_STORE_PROPERTY_NAME;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.security.SecureRandom;
import java.util.Properties;

public class HashingTest {

	private final static int ITERATION_NUMBER = 1000;

	private static final Logger LOG = LoggerFactory.getLogger(ServerIntegrationMqttFceTest.class);

	static MqttClientPersistence s_dataStore;
	static MqttClientPersistence s_pubDataStore;

	Server m_server;
	IMqttClient m_client;
	TestCallback m_callback;
	IConfig m_config;

	@BeforeClass
	public static void beforeTests() {
		String tmpDir = System.getProperty("java.io.tmpdir");
		s_dataStore = new MqttDefaultFilePersistence(tmpDir);
		s_pubDataStore = new MqttDefaultFilePersistence(tmpDir + File.separator + "publisher");
	}

	protected void startServer() throws IOException {
		m_server = new Server();
		final Properties configProps = IntegrationUtils.prepareTestPropeties();
		m_config = new MemoryConfig(configProps);
		m_server.startServer(m_config);
	}

	@Before
	public void setUp() throws Exception {
		String dbPath = IntegrationUtils.localMapDBPath();
		File dbFile = new File(dbPath);
		assertFalse(String.format("The DB storagefile %s already exists", dbPath), dbFile.exists());

		startServer();

		m_client = new MqttClient("tcp://localhost:1883", "TestClient", s_dataStore);
		m_callback = new TestCallback();
		m_client.setCallback(m_callback);
	}

	@After
	public void tearDown() throws Exception {
		if (m_client.isConnected()) {
			m_client.disconnect();
		}

		String dbPath = IntegrationUtils.localMapDBPath();
		File dbFile = new File(dbPath);
		if (dbFile.exists()) {
			dbFile.delete();
		}
		assertFalse(dbFile.exists());

		stopServer();
	}

	private void stopServer() {
		m_server.stopServer();
		File dbFile = new File(m_config.getProperty(PERSISTENT_STORE_PROPERTY_NAME));
		if (dbFile.exists()) {
			dbFile.delete();
		}
		assertFalse(dbFile.exists());
	}

	/**
	 * From a password, a number of iterations and a salt, returns the
	 * corresponding digest
	 * 
	 * @param iterationNb
	 *            int The number of iterations of the algorithm
	 * @param password
	 *            String The password to encrypt
	 * @param salt
	 *            byte[] The salt
	 * @return byte[] The digested password
	 * @throws NoSuchAlgorithmException
	 *             If the algorithm doesn't exist
	 * @throws UnsupportedEncodingException
	 */
	public byte[] getHash(int iterationNb, String password, byte[] salt)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		digest.reset();
		digest.update(salt);
		byte[] input = digest.digest(password.getBytes("UTF-8"));
		for (int i = 0; i < iterationNb; i++) {
			digest.reset();
			input = digest.digest(input);
		}
		return input;
	}

	/**
	 * From a base 64 representation, returns the corresponding byte[]
	 * 
	 * @param data
	 *            String The base64 representation
	 * @return byte[]
	 * @throws IOException
	 */
	public static byte[] base64ToByte(String data) throws IOException {
		BASE64Decoder decoder = new BASE64Decoder();
		return decoder.decodeBuffer(data);
	}

	/**
	 * From a byte[] returns a base 64 representation
	 * 
	 * @param data
	 *            byte[]
	 * @return String
	 * @throws IOException
	 */
	public static String byteToBase64(byte[] data) {
		BASE64Encoder endecoder = new BASE64Encoder();
		return endecoder.encode(data);
	}

	@Test
	public void testHashing() throws Exception {
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		byte[] bSalt = new byte[8];

		random.nextBytes(bSalt);
		// Digest computation
		byte[] bDigest = getHash(ITERATION_NUMBER, "test2134!", bSalt);
		String pw = byteToBase64(bDigest);
		String sSalt = byteToBase64(bSalt);

		System.out.println(pw);
		System.out.println(sSalt);

		// Digest computation
		byte[] bDigest2 = getHash(ITERATION_NUMBER, "342fafasf!", bSalt);
		String pw2 = byteToBase64(bDigest2);
		String sSalt2 = byteToBase64(bSalt);

		System.out.println(pw2);
		System.out.println(sSalt2);

		assertFalse(pw.equalsIgnoreCase(pw2));

		byte[] bDigest3 = getHash(ITERATION_NUMBER, "test2134!", bSalt);
		String pw3 = byteToBase64(bDigest3);
		String sSalt3 = byteToBase64(bSalt);

		assertTrue(pw3.equalsIgnoreCase(pw));
	}

	@Test
	public void testCanCreadHashedPwTopic() throws Exception {
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		byte[] bSalt = new byte[8];

		random.nextBytes(bSalt);
		// Digest computation
		byte[] bDigest = getHash(ITERATION_NUMBER, "test2134!", bSalt);
		String pw = byteToBase64(bDigest);
		String sSalt = byteToBase64(bSalt);

		LOG.info("*** testSubscribe ***");
		MqttConnectOptions options = new MqttConnectOptions();
		options.setUserName("user");
		options.setPassword("pw".toCharArray());
		m_client.connect(options);

		JSONObject confInit = new JSONObject();
		confInit.put("init", "true");
		confInit.put("permissions", "rw");

		MqttMessage initialManagedMessage = new MqttMessage(confInit.toString().getBytes());
		initialManagedMessage.setQos(0);
		initialManagedMessage.setRetained(false);

		m_client.publish("/" + pw + "." + sSalt, initialManagedMessage);

		String tmpDir = System.getProperty("java.io.tmpdir");

		MqttClientPersistence dsSubscriberA = new MqttDefaultFilePersistence(tmpDir + File.separator + "subscriberA");
		MqttClientPersistence dsSubscriberB = new MqttDefaultFilePersistence(tmpDir + File.separator + "subscriberB");

		MqttClient subscriberAwithoutCredentials = new MqttClient("tcp://localhost:1883", "SubscriberA", dsSubscriberA);
		TestCallback cbSubscriberAwithoutCredentials = new TestCallback();
		subscriberAwithoutCredentials.setCallback(cbSubscriberAwithoutCredentials);
		subscriberAwithoutCredentials.connect();

		MqttClient subscriberWithCredentials = new MqttClient("tcp://localhost:1883", "SubscriberB", dsSubscriberB);
		TestCallback cbSubscriberWithCredentials = new TestCallback();
		subscriberWithCredentials.setCallback(cbSubscriberWithCredentials);
		subscriberWithCredentials.connect(options);

		subscriberAwithoutCredentials.subscribe("/" + pw + "." + sSalt, 1);
		subscriberWithCredentials.subscribe("/" + pw + "." + sSalt, 1);

		JSONObject blaJson = new JSONObject();
		confInit.put("bla", "bla");

		MqttMessage bla = new MqttMessage(blaJson.toString().getBytes());
		initialManagedMessage.setQos(0);
		initialManagedMessage.setRetained(false);
		m_client.publish("/" + pw + "." + sSalt, bla);

		MqttMessage messageOnA = cbSubscriberAwithoutCredentials.getMessage(false);
		assertNull(messageOnA);
		subscriberAwithoutCredentials.disconnect();

		MqttMessage messageOnB = cbSubscriberWithCredentials.getMessage(true);
		assertNotNull(messageOnB);
		assertEquals(1, messageOnB.getQos());
		subscriberWithCredentials.disconnect();

		// able to handle reconnect?

		subscriberWithCredentials.connect(options);

		m_client.publish("/" + pw + "." + sSalt, bla);
		MqttMessage messageOnB2 = cbSubscriberWithCredentials.getMessage(true);
		assertNotNull(messageOnB2);
		assertEquals(1, messageOnB2.getQos());
		subscriberWithCredentials.disconnect();

	}
}
