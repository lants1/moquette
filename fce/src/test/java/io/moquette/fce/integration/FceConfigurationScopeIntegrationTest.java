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
package io.moquette.fce.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


import javax.net.ssl.SSLSocketFactory;
import io.moquette.fce.common.ReadFileUtil;
import io.moquette.fce.model.common.ManagedZone;
import io.moquette.fce.model.info.InfoMessageType;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.junit.Test;

/**
 * 
 * Integration tests for private configuration functionality...
 * 
 * @author lants1
 *
 */
public class FceConfigurationScopeIntegrationTest extends FceIntegrationTest {

	@Test
	public void checkGlobalConfig() throws Exception {
		SSLSocketFactory ssf = configureSSLSocketFactory();

		MqttConnectOptions options = new MqttConnectOptions();
		options.setUserName(USERNAME);
		options.setPassword(services.getHashing().generateHash(USERNAME).toCharArray());
		options.setSocketFactory(ssf);
		m_client.connect(options);

		String topic = "/FceConfigurationScopeIntegrationTest/lemanage";
		String intentTopic = ManagedZone.INTENT.getTopicPrefix() + topic;
		String infoTopic = ManagedZone.INFO.getTopicPrefix() + topic + "/#";

		m_client.subscribe(topic, 0);
		m_client.subscribe(infoTopic, 0);

		String inputJson = ReadFileUtil.readFileString("/integration/restriction_manage_global.json");
		
		m_client.publish(intentTopic, inputJson.getBytes(), 0, true);
		assertTrue(
				StringUtils.contains(m_callback.getMessage(false).toString(), InfoMessageType.TOPIC_CONFIGURATION_ACCEPTED.getValue()));
		m_callback.reinit();

		m_client.publish(topic, SAMPLE_MESSAGE.getBytes(), 0, true);
		assertEquals(SAMPLE_MESSAGE, m_callback.getMessage(true).toString());
		m_callback.reinit();
		
		m_client.publish(topic, SAMPLE_MESSAGE.getBytes(), 0, true);
		assertEquals(SAMPLE_MESSAGE, m_callback.getMessage(true).toString());
		m_callback.reinit();
		
		m_client.publish(topic, SAMPLE_MESSAGE.getBytes(), 0, true);
		assertTrue(StringUtils.contains(m_callback.getMessage(true).toString(), InfoMessageType.GLOBAL_QUOTA_DEPLETED.getValue()));
		m_callback.reinit();

		m_client.disconnect();
	}
	
	@Test
	public void checkGlobalConfigAll() throws Exception {
		SSLSocketFactory ssf = configureSSLSocketFactory();

		MqttConnectOptions options = new MqttConnectOptions();
		options.setUserName(USERNAME);
		options.setPassword(services.getHashing().generateHash(USERNAME).toCharArray());
		options.setSocketFactory(ssf);
		m_client.connect(options);

		String topic = "/FceConfigurationScopeIntegrationTest/siasdfmplemanage1all";
		String intentTopic = ManagedZone.INTENT.getTopicPrefix() + topic;
		String infoTopic = ManagedZone.INFO.getTopicPrefix() + topic + "/#";

		m_client.subscribe(topic, 0);
		m_client.subscribe(infoTopic, 0);

		Thread.sleep(500);
		
		String inputJson = ReadFileUtil.readFileString("/integration/restriction_manage_global_all.json");

		m_client.publish(intentTopic, inputJson.getBytes(), 0, true);
		assertTrue(
				StringUtils.contains(m_callback.getMessage(true).toString(), InfoMessageType.TOPIC_CONFIGURATION_ACCEPTED.getValue()));
		m_callback.reinit();

		m_client.publish(topic, SAMPLE_MESSAGE.getBytes(), 0, true);
		assertEquals(SAMPLE_MESSAGE, m_callback.getMessage(true).toString());
		m_callback.reinit();
		
		m_client.publish(topic, SAMPLE_MESSAGE.getBytes(), 0, true);
		assertEquals(SAMPLE_MESSAGE, m_callback.getMessage(true).toString());
		m_callback.reinit();
		
		m_client.publish(topic, SAMPLE_MESSAGE.getBytes(), 0, true);
		assertTrue(StringUtils.contains(m_callback.getMessage(true).toString(), InfoMessageType.GLOBAL_QUOTA_DEPLETED.getValue()));
		m_callback.reinit();
		
		
		MqttClient secondClient = new MqttClient("ssl://localhost:8883", "secondTestClient123", new MemoryPersistence());
		MqttConnectOptions secondClientOptions = new MqttConnectOptions();
		secondClientOptions.setUserName(OTHER_USERNAME);
		secondClientOptions.setPassword(services.getHashing().generateHash(OTHER_USERNAME).toCharArray());
		secondClientOptions.setSocketFactory(ssf);
		secondClient.connect(secondClientOptions);
		FceTestCallback m_callback2 = new FceTestCallback();
		secondClient.setCallback(m_callback2);
		secondClient.subscribe(infoTopic, 0);
		secondClient.subscribe(topic, 0);
		
		m_callback2.reinit();
		secondClient.publish(topic, SAMPLE_MESSAGE.getBytes(), 0, true);
		assertEquals(SAMPLE_MESSAGE, m_callback2.getMessage(true).toString());
		m_callback2.reinit();
		
		secondClient.publish(topic, SAMPLE_MESSAGE.getBytes(), 0, true);
		assertEquals(SAMPLE_MESSAGE, m_callback2.getMessage(true).toString());
		m_callback2.reinit();
		
		secondClient.publish(topic, SAMPLE_MESSAGE.getBytes(), 0, true);
		assertTrue(StringUtils.contains(m_callback2.getMessage(true).toString(), InfoMessageType.GLOBAL_QUOTA_DEPLETED.getValue()));
		m_callback2.reinit();
		
		secondClient.disconnect();
		m_client.disconnect();
	}
	
	@Test
	public void checkPrivateConfig() throws Exception {
		SSLSocketFactory ssf = configureSSLSocketFactory();

		MqttConnectOptions options = new MqttConnectOptions();
		options.setUserName(USERNAME);
		options.setPassword(services.getHashing().generateHash(USERNAME).toCharArray());
		options.setSocketFactory(ssf);
		m_client.connect(options);

		String topic = "/FceConfigurationScopeIntegrationTest/simplemanage2";
		String intentTopic = ManagedZone.INTENT.getTopicPrefix() + topic;
		String infoTopic = ManagedZone.INFO.getTopicPrefix() + topic + "/#";

		m_client.subscribe(topic, 0);
		m_client.subscribe(infoTopic, 0);

		String inputJsonGlobal = ReadFileUtil.readFileString("/integration/restriction_manage_global.json");
		String inputJsonPrivate = ReadFileUtil.readFileString("/integration/restriction_manage_private.json");
		
		m_callback.reinit();
		m_client.publish(intentTopic, inputJsonGlobal.getBytes(), 0, true);
		assertTrue(
				StringUtils.contains(m_callback.getMessage(true).toString(), InfoMessageType.TOPIC_CONFIGURATION_ACCEPTED.getValue()));
		m_callback.reinit();

		m_client.publish(intentTopic, inputJsonPrivate.getBytes(), 0, true);
		assertTrue(
				StringUtils.contains(m_callback.getMessage(true).toString(), InfoMessageType.TOPIC_CONFIGURATION_ACCEPTED.getValue()));
		m_callback.reinit();
		
		m_client.publish(topic, SAMPLE_MESSAGE.getBytes(), 0, true);
		assertEquals(SAMPLE_MESSAGE, m_callback.getMessage(true).toString());
		m_callback.reinit();
		
		m_client.publish(topic, SAMPLE_MESSAGE.getBytes(), 0, true);
		assertTrue(StringUtils.contains(m_callback.getMessage(true).toString(), InfoMessageType.PRIVATE_QUOTA_DEPLETED.getValue()));
		m_callback.reinit();

		m_client.disconnect();
	}
	
	@Test
	public void checkConfigAlreadyManaged() throws Exception {
		SSLSocketFactory ssf = configureSSLSocketFactory();

		MqttConnectOptions options = new MqttConnectOptions();
		options.setUserName(USERNAME);
		options.setPassword(services.getHashing().generateHash(USERNAME).toCharArray());
		options.setSocketFactory(ssf);
		m_client.connect(options);

		String topic = "/FceConfigurationScopeIntegrationTest/simplemanage3";
		String intentTopic = ManagedZone.INTENT.getTopicPrefix() + topic;
		String infoTopic = ManagedZone.INFO.getTopicPrefix() + topic + "/#";

		m_client.subscribe(topic, 0);
		m_client.subscribe(infoTopic, 0);

		String inputJsonGlobal = ReadFileUtil.readFileString("/integration/restriction_manage_global.json");
		String inputJsonPrivate = ReadFileUtil.readFileString("/integration/restriction_manage_private.json");
		
		// global config
		m_callback.reinit();
		m_client.publish(intentTopic, inputJsonGlobal.getBytes(), 0, true);
		assertTrue(
				StringUtils.contains(m_callback.getMessage(true).toString(), InfoMessageType.TOPIC_CONFIGURATION_ACCEPTED.getValue()));
		m_callback.reinit();

		// private config
		m_client.publish(intentTopic, inputJsonPrivate.getBytes(), 0, true);
		assertTrue(
				StringUtils.contains(m_callback.getMessage(true).toString(), InfoMessageType.TOPIC_CONFIGURATION_ACCEPTED.getValue()));
		m_callback.reinit();
		
		Thread.sleep(500);
		
		// publish
		m_client.publish(topic, SAMPLE_MESSAGE.getBytes(), 0, true);
		assertEquals(SAMPLE_MESSAGE, m_callback.getMessage(true).toString());
		m_callback.reinit();
		
		// new global config
		m_client.publish(intentTopic, inputJsonGlobal.getBytes(), 0, true);
		assertTrue(
				StringUtils.contains(m_callback.getMessage(true).toString(), InfoMessageType.TOPIC_CONFIGURATION_ACCEPTED.getValue()));
		m_callback.reinit();

		Thread.sleep(500);
		
		// publish
		m_client.publish(topic, SAMPLE_MESSAGE.getBytes(), 0, true);
		assertTrue(StringUtils.contains(m_callback.getMessage(true).toString(), InfoMessageType.PRIVATE_QUOTA_DEPLETED.getValue()));
		m_callback.reinit();

		// new private config
		m_client.publish(intentTopic, inputJsonPrivate.getBytes(), 0, true);
		assertTrue(
				StringUtils.contains(m_callback.getMessage(true).toString(), InfoMessageType.TOPIC_CONFIGURATION_ACCEPTED.getValue()));
		m_callback.reinit();
		
		Thread.sleep(500);
		
		// publish
		m_client.publish(topic, SAMPLE_MESSAGE.getBytes(), 0, true);
		assertEquals(SAMPLE_MESSAGE, m_callback.getMessage(true).toString());
		m_callback.reinit();
		
		// new private config
		m_client.publish(intentTopic, inputJsonPrivate.getBytes(), 0, true);
		assertTrue(
				StringUtils.contains(m_callback.getMessage(true).toString(), InfoMessageType.TOPIC_CONFIGURATION_ACCEPTED.getValue()));
		m_callback.reinit();
		
		Thread.sleep(500);
		
		// publish
		m_client.publish(topic, SAMPLE_MESSAGE.getBytes(), 0, true);
		assertEquals(SAMPLE_MESSAGE, m_callback.getMessage(true).toString());
		m_callback.reinit();
		
		// publish
		m_client.publish(topic, SAMPLE_MESSAGE.getBytes(), 0, true);
		assertTrue(StringUtils.contains(m_callback.getMessage(true).toString(), InfoMessageType.GLOBAL_QUOTA_DEPLETED.getValue()));
		m_callback.reinit();
		
		m_client.disconnect();
	}
	
	@Test
	public void changePrivateConfigWhenManaged() throws Exception {
		LOG.info("*** checkSupportSSL ***");
		SSLSocketFactory ssf = configureSSLSocketFactory();

		MqttConnectOptions options = new MqttConnectOptions();
		options.setUserName(USERNAME);
		options.setPassword(services.getHashing().generateHash(USERNAME).toCharArray());
		options.setSocketFactory(ssf);
		m_client.connect(options);

		String topic = "/FceEnhancedServerIntegrationTest/simplemanage";
		String intentTopic = ManagedZone.INTENT.getTopicPrefix() + topic;
		String infoTopic = ManagedZone.INFO.getTopicPrefix() + topic + "/#";

		m_client.subscribe(topic, 0);
		m_client.subscribe(infoTopic, 0);

		String inputJson = ReadFileUtil.readFileString("/integration/minimal_manage.json");
		String inputPrivate = ReadFileUtil.readFileString("/integration/restriction_manage_private.json");
		
		// public
		m_client.publish(intentTopic, inputJson.getBytes(), 0, true);
		assertTrue(
				StringUtils.contains(m_callback.getMessage(true).toString(), InfoMessageType.TOPIC_CONFIGURATION_ACCEPTED.getValue()));
		m_callback.reinit();
		
		Thread.sleep(1000);
		
		// private
		m_client.publish(intentTopic, inputPrivate.getBytes(), 0, true);
		assertTrue(
				StringUtils.contains(m_callback.getMessage(true).toString(), InfoMessageType.TOPIC_CONFIGURATION_ACCEPTED.getValue()));
		m_callback.reinit();

		MqttClient secondClient = new MqttClient("ssl://localhost:8883", "secondTestClient77", new MemoryPersistence());
		MqttConnectOptions secondClientOptions = new MqttConnectOptions();
		secondClientOptions.setUserName(OTHER_USERNAME);
		secondClientOptions.setPassword(services.getHashing().generateHash(OTHER_USERNAME).toCharArray());
		secondClientOptions.setSocketFactory(ssf);
		secondClient.connect(secondClientOptions);
		FceTestCallback m_callback2 = new FceTestCallback();
		secondClient.setCallback(m_callback2);
		secondClient.subscribe(infoTopic, 0);
		
		String inputJsonOtheruser = ReadFileUtil.readFileString("/integration/restriction_manage_private_otheruser.json");
		
		secondClient.publish(intentTopic, inputJsonOtheruser.getBytes(), 0, true);
		assertTrue(
				StringUtils.contains(m_callback2.getMessage(true).toString(), InfoMessageType.TOPIC_CONFIGURATION_ACCEPTED.getValue()));
		m_callback2.reinit();

		m_client.disconnect();
		secondClient.disconnect();
	}
}
