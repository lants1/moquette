package org.eclipse.moquette.server;
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


import org.eclipse.moquette.server.config.IConfig;
import org.eclipse.moquette.server.config.MemoryConfig;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import static org.eclipse.moquette.commons.Constants.*;
import static org.junit.Assert.*;

public class ServerIntegrationMqttFceTest {

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

    @Test
    public void testFirstPublishOnTopicWithCredentialsButWithoutManagedInitialisation() throws Exception {
        LOG.info("*** testSubscribe ***");
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName("user");
        options.setPassword("pw".toCharArray());
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
    }
    
    @Ignore
    @Test(expected=MqttException.class)
    public void testFirstPublishOnTopicWithCredentialsWithManagedInitialisation() throws Exception {
        LOG.info("*** testSubscribe ***");
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName("user");
        options.setPassword("pw".toCharArray());
        m_client.connect(options);
        
        
        String secretstring = "{ \"secret\": \"pw\" }";
        JSONObject secretjson = new JSONObject(secretstring);
        
        MqttMessage initialManagedMessage = new MqttMessage(secretjson.toString().getBytes());
        initialManagedMessage.setQos(0);
        initialManagedMessage.setRetained(false);
        
        m_client.publish("/managed", initialManagedMessage);
        
        String tmpDir = System.getProperty("java.io.tmpdir");

        MqttClientPersistence dsSubscriberA = new MqttDefaultFilePersistence(tmpDir + File.separator + "subscriberA");

        MqttClient subscriberAwithoutCredentials = new MqttClient("tcp://localhost:1883", "SubscriberA", dsSubscriberA);
        TestCallback cbSubscriberAwithoutCredentials = new TestCallback();
        subscriberAwithoutCredentials.setCallback(cbSubscriberAwithoutCredentials);
        subscriberAwithoutCredentials.connect();
        
        
        // Should fail: Subscription to a managed topic without permissions is not possible.
        subscriberAwithoutCredentials.subscribe("/managed", 1);
    }
    
}