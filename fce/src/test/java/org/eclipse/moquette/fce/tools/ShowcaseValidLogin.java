package org.eclipse.moquette.fce.tools;

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

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.eclipse.moquette.fce.common.FceHashUtil;
import org.eclipse.moquette.fce.tools.callback.SampleFceClientCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

public class ShowcaseValidLogin {

	private static String USERNAME = "user";
	private static String PASSWORD = "password";
	
	public static void main(String[] args) throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException, KeyStoreException, MqttException, IOException {
		initializeInternalMqttClient();
		
	}

	public static void initializeInternalMqttClient() throws MqttException, KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, CertificateException, KeyStoreException, IOException {
			
		MqttClient client;
		client = new MqttClient("ssl://localhost:8883", FceHashUtil.getFceHash(USERNAME, PASSWORD));

			SSLSocketFactory ssf = configureSSLSocketFactory();

			MqttConnectOptions options = new MqttConnectOptions();
			options.setUserName(USERNAME);
			options.setPassword(PASSWORD.toCharArray());
			options.setSocketFactory(ssf);

			client.connect(options);
			client.setCallback(new SampleFceClientCallback());
			client.publish("/test1", "test".getBytes(), 0, false);

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
	private static SSLSocketFactory configureSSLSocketFactory() throws KeyManagementException,
			NoSuchAlgorithmException, UnrecoverableKeyException, IOException, CertificateException, KeyStoreException {
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
		URL jksUrl = ShowcaseValidLogin.class.getClassLoader().getResource(jksPath);
		File jksFile = new File(jksPath);
		if (jksFile.exists()) {
			return new FileInputStream(jksFile);
		}

		if (jksUrl != null) {
			return ShowcaseValidLogin.class.getClassLoader().getResourceAsStream(jksPath);
		}
		throw new RuntimeException();
	}
	

}
