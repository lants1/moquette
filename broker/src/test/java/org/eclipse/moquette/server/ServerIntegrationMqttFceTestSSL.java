/*
 * Copyright (c) 2012-2015 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */
package org.eclipse.moquette.server;

import static org.eclipse.moquette.commons.Constants.JKS_PATH_PROPERTY_NAME;
import static org.eclipse.moquette.commons.Constants.KEY_MANAGER_PASSWORD_PROPERTY_NAME;
import static org.eclipse.moquette.commons.Constants.KEY_STORE_PASSWORD_PROPERTY_NAME;
import static org.eclipse.moquette.commons.Constants.PERSISTENT_STORE_PROPERTY_NAME;
import static org.eclipse.moquette.commons.Constants.SSL_PORT_PROPERTY_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Check that Moquette could also handle SSL.
 * 
 * @author andrea
 */
public class ServerIntegrationMqttFceTestSSL {
    private static final Logger LOG = LoggerFactory.getLogger(ServerIntegrationSSLTest.class);

    Server m_server;
    static MqttClientPersistence s_dataStore;

    IMqttClient m_client;
    TestCallback m_callback;

    @BeforeClass
    public static void beforeTests() {
        String tmpDir = System.getProperty("java.io.tmpdir");
        s_dataStore = new MqttDefaultFilePersistence(tmpDir);
    }

    protected void startServer() throws IOException {
        String file = getClass().getResource("/").getPath();
        System.setProperty("moquette.path", file);
        m_server = new Server();

        Properties sslProps = new Properties();
        sslProps.put(SSL_PORT_PROPERTY_NAME, "8883");
        sslProps.put(JKS_PATH_PROPERTY_NAME, "serverkeystore.jks");
        sslProps.put(KEY_STORE_PASSWORD_PROPERTY_NAME, "passw0rdsrv");
        sslProps.put(KEY_MANAGER_PASSWORD_PROPERTY_NAME, "passw0rdsrv");
        sslProps.put(PERSISTENT_STORE_PROPERTY_NAME, IntegrationUtils.localMapDBPath());
        m_server.startServer(sslProps);
    }

    @Before
    public void setUp() throws Exception {
        String dbPath = IntegrationUtils.localMapDBPath();
        File dbFile = new File(dbPath);
        assertFalse(String.format("The DB storagefile %s already exists", dbPath), dbFile.exists());

        startServer();

        m_client = new MqttClient("ssl://localhost:8883", "TestClient", s_dataStore);
//        m_client = new MqttClient("ssl://test.mosquitto.org:8883", "TestClient", s_dataStore);
        
        m_callback = new TestCallback();
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
        String dbPath = IntegrationUtils.localMapDBPath();
        File dbFile = new File(dbPath);
        if (dbFile.exists()) {
            dbFile.delete();
        }
        assertFalse(dbFile.exists());
    }


    @Test
    public void testFirstPublishOnTopicWithCredentialsButWithoutManagedInitialisation() throws Exception {
        LOG.info("*** testSubscribe ***");
        SSLSocketFactory ssf = configureSSLSocketFactory();
        
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName("user");
        options.setPassword("pw".toCharArray());
        options.setSocketFactory(ssf);
        m_client.connect(options);
        
        MqttMessage initialManagedMessage = new MqttMessage("Hello world!!".getBytes());
        initialManagedMessage.setQos(0);
        initialManagedMessage.setRetained(false);
        
        m_client.publish("/managed", initialManagedMessage);
        
        String tmpDir = System.getProperty("java.io.tmpdir");

        MqttClientPersistence dsSubscriberA = new MqttDefaultFilePersistence(tmpDir + File.separator + "subscriberA");

        MqttClient subscriberA = new MqttClient("tcp://localhost:1883", "SubscriberA", dsSubscriberA);
        TestCallback cbSubscriberA = new TestCallback();
        subscriberA.setCallback(cbSubscriberA);
        subscriberA.connect();
        subscriberA.subscribe("/managed", 1);

        MqttMessage firstManagedMessage = new MqttMessage("Hello world!!".getBytes());
        firstManagedMessage.setQos(1);
        firstManagedMessage.setRetained(false);
        
        m_client.publish("/managed", firstManagedMessage);
        
        MqttMessage messageOnA = cbSubscriberA.getMessage(true);
        assertEquals("Hello world!!", new String(messageOnA.getPayload()));
        assertEquals(1, messageOnA.getQos());
        subscriberA.disconnect();
        m_client.disconnect();
    }
    
    @Test
    public void testFirstPublishOnTopicWithCredentialsWithManagedInitialisation() throws Exception {
        LOG.info("*** testSubscribe ***");
        SSLSocketFactory ssf = configureSSLSocketFactory();
        
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName("user");
        options.setPassword("pw".toCharArray());
        options.setSocketFactory(ssf);
        m_client.connect(options);
        
        JSONObject confInit=new JSONObject();
        confInit.put("init","true");
        confInit.put("permissions", "rw");
        
        MqttMessage initialManagedMessage = new MqttMessage(confInit.toString().getBytes());
        initialManagedMessage.setQos(0);
        initialManagedMessage.setRetained(false);
        
        m_client.publish("/readonlytopic", initialManagedMessage);
        
        String tmpDir = System.getProperty("java.io.tmpdir");

        MqttClientPersistence dsSubscriberA = new MqttDefaultFilePersistence(tmpDir + File.separator + "subscriberA");
        MqttClientPersistence dsSubscriberB = new MqttDefaultFilePersistence(tmpDir + File.separator + "subscriberB");
        
        MqttClient subscriberAwithoutCredentials = new MqttClient("tcp://localhost:1883", "SubscriberA", dsSubscriberA);
        TestCallback cbSubscriberAwithoutCredentials = new TestCallback();
        subscriberAwithoutCredentials.setCallback(cbSubscriberAwithoutCredentials);
        subscriberAwithoutCredentials.connect();
        
        MqttClient subscriberWithCredentials = new MqttClient("ssl://localhost:8883", "SubscriberB", dsSubscriberB);
        TestCallback cbSubscriberWithCredentials = new TestCallback();
        subscriberWithCredentials.setCallback(cbSubscriberWithCredentials);
        subscriberWithCredentials.connect(options);
        
        subscriberAwithoutCredentials.subscribe("/readonlytopic", 1);
        subscriberWithCredentials.subscribe("/readonlytopic", 1);
        
        JSONObject blaJson=new JSONObject();
        confInit.put("bla","bla");
        
        MqttMessage bla = new MqttMessage(blaJson.toString().getBytes());
        initialManagedMessage.setQos(0);
        initialManagedMessage.setRetained(false);
        m_client.publish("/readonlytopic", bla);
        
        MqttMessage messageOnA = cbSubscriberAwithoutCredentials.getMessage(false);
        assertNull(messageOnA);
        subscriberAwithoutCredentials.disconnect();
        
        MqttMessage messageOnB = cbSubscriberWithCredentials.getMessage(true);
        assertNotNull(messageOnB);
        assertEquals(1, messageOnB.getQos());
        subscriberWithCredentials.disconnect();
        
        // able to handle reconnect?
        
        subscriberWithCredentials.connect(options);

        m_client.publish("/readonlytopic", bla);
        MqttMessage messageOnB2 = cbSubscriberWithCredentials.getMessage(true);
        assertNotNull(messageOnB2);
        assertEquals(1, messageOnB2.getQos());
        subscriberWithCredentials.disconnect();
        m_client.disconnect();
    }
    
    /**
     * keystore generated into test/resources with command:
     * 
     * keytool -keystore clientkeystore.jks -alias testclient -genkey -keyalg RSA
     * -> mandatory to put the name surname
     * -> password is passw0rd
     * -> type yes at the end
     * 
     * to generate the crt file from the keystore
     * -- keytool -certreq -alias testclient -keystore clientkeystore.jks -file testclient.csr
     * 
     * keytool -export -alias testclient -keystore clientkeystore.jks -file testclient.crt
     * 
     * to import an existing certificate:
     * keytool -keystore clientkeystore.jks -import -alias testclient -file testclient.crt -trustcacerts
     */
    private SSLSocketFactory configureSSLSocketFactory() throws KeyManagementException, NoSuchAlgorithmException, UnrecoverableKeyException, IOException, CertificateException, KeyStoreException {
        KeyStore ks = KeyStore.getInstance("JKS");
        InputStream jksInputStream = getClass().getClassLoader().getResourceAsStream("clientkeystore.jks");
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