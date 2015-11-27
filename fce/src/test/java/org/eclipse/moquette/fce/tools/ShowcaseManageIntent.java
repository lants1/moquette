package org.eclipse.moquette.fce.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.eclipse.moquette.fce.common.FceHashUtil;
import org.eclipse.moquette.fce.common.ManagedZone;
import org.eclipse.moquette.fce.service.JsonParserService;
import org.eclipse.moquette.fce.tools.callback.SampleFceClientCallback;
import org.eclipse.moquette.plugin.AuthenticationProperties;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

public class ShowcaseManageIntent {

	private static String USERNAME = "user";
	private static String PASSWORD = "password";

	public static void main(String[] args) throws Exception {
		initializeInternalMqttClient();

	}

	public static void initializeInternalMqttClient() throws Exception {

		MqttClient client;
		client = new MqttClient("ssl://localhost:8883", FceHashUtil.getFceHash(USERNAME, PASSWORD));

		System.out.println(FceHashUtil.getFceHash("fceplugin", "samplepw"));
		System.out.println(FceHashUtil.validateClientIdHash(new AuthenticationProperties("fceplugin", "samplepw".getBytes(), FceHashUtil.getFceHash("fceplugin", "samplepw"))));
		
		
		SSLSocketFactory ssf = configureSSLSocketFactory();

		MqttConnectOptions options = new MqttConnectOptions();
		options.setUserName(USERNAME);
		options.setPassword(PASSWORD.toCharArray());
		options.setSocketFactory(ssf);

		client.connect(options);
		client.setCallback(new SampleFceClientCallback());
		
		String inputJson = readFile("/showcase_manage.json");
		
		
		client.publish(ManagedZone.INTENT.getTopicPrefix()+"/test1", inputJson.getBytes(), 2, false);

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
	private static SSLSocketFactory configureSSLSocketFactory() throws KeyManagementException, NoSuchAlgorithmException,
			UnrecoverableKeyException, IOException, CertificateException, KeyStoreException {
		KeyStore ks = KeyStore.getInstance("JKS");

		InputStream jksInputStream = jksDatastore("pluginkeystore.jks");
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

	private static InputStream jksDatastore(String jksPath) throws FileNotFoundException {
		URL jksUrl = ShowcaseManageIntent.class.getClassLoader().getResource(jksPath);
		File jksFile = new File(jksPath);
		if (jksFile.exists()) {
			return new FileInputStream(jksFile);
		}

		if (jksUrl != null) {
			return ShowcaseManageIntent.class.getClassLoader().getResourceAsStream(jksPath);
		}
		throw new RuntimeException();
	}
	
	private static String readFile(String path) throws IOException, URISyntaxException {
		byte[] encoded = Files.readAllBytes(Paths.get(ShowcaseManageIntent.class.getResource(path).toURI()));
		return new String(encoded, StandardCharsets.UTF_8);
	}

}
