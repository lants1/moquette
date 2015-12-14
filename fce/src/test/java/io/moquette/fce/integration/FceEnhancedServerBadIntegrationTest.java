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

import static org.junit.Assert.assertTrue;

import javax.net.ssl.SSLSocketFactory;
import io.moquette.fce.common.ReadFileUtil;
import io.moquette.fce.model.common.ManagedZone;
import io.moquette.fce.model.info.InfoMessageType;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.junit.Test;


/**
 * Integrationtests for basic fce loading and handling functionality...
 * 
 * @author lants1
 *
 */
public class FceEnhancedServerBadIntegrationTest extends FceIntegrationTest {

	@Test
	public void corruptConfig() throws Exception {
		SSLSocketFactory ssf = configureSSLSocketFactory();

		MqttConnectOptions options = new MqttConnectOptions();
		options.setUserName(USERNAME);
		options.setPassword(services.getHashing().generateHash(USERNAME).toCharArray());
		options.setSocketFactory(ssf);
		m_client.connect(options);

		String topic = "/FceEnhancedServerBadIntegrationTest/simplemanage";
		String intentTopic = ManagedZone.INTENT.getTopicPrefix() + topic;
		String infoTopic = ManagedZone.INFO.getTopicPrefix() + topic + "/#";

		m_client.subscribe(topic, 0);
		m_client.subscribe(infoTopic, 0);

		String inputJson = ReadFileUtil.readFileString("/integration/corrupt_config.json");

		m_client.publish(intentTopic, inputJson.getBytes(), 0, true);
		assertTrue(
				StringUtils.contains(m_callback.getMessage(true).toString(), InfoMessageType.AUTHORIZATION_EXCEPTION.getValue()));
		m_callback.reinit();

		m_client.disconnect();
	}
	
	@Test
	public void noManagePermission() throws Exception {
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

		m_client.publish(intentTopic, inputJson.getBytes(), 0, true);
		assertTrue(
				StringUtils.contains(m_callback.getMessage(true).toString(), InfoMessageType.TOPIC_CONFIGURATION_ACCEPTED.getValue()));
		m_callback.reinit();

		MqttClient secondClient = new MqttClient("ssl://localhost:8883", "secondTestClient", new MemoryPersistence());
		MqttConnectOptions secondClientOptions = new MqttConnectOptions();
		secondClientOptions.setUserName(OTHER_USERNAME);
		secondClientOptions.setPassword(services.getHashing().generateHash(OTHER_USERNAME).toCharArray());
		secondClientOptions.setSocketFactory(ssf);
		secondClient.connect(secondClientOptions);
		FceTestCallback m_callback2 = new FceTestCallback();
		secondClient.setCallback(m_callback2);
		secondClient.subscribe(infoTopic, 0);
		
		String inputJsonOtheruser = ReadFileUtil.readFileString("/integration/minimal_manage_otheruser.json");
		
		secondClient.publish(intentTopic, inputJsonOtheruser.getBytes(), 0, true);
		assertTrue(
				StringUtils.contains(m_callback2.getMessage(true).toString(), InfoMessageType.MISSING_ADMIN_RIGHTS.getValue()));
		m_callback2.reinit();

		m_client.disconnect();
		secondClient.disconnect();
	}
	
	@Test
	public void changePrivateConfigFromOtherUser() throws Exception {
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
		
		Thread.sleep(1000);
		
		String inputJsonOtheruser = ReadFileUtil.readFileString("/integration/restriction_manage_private_otheruser.json");
		
		m_client.publish(intentTopic, inputJsonOtheruser.getBytes(), 0, true);
		assertTrue(
				StringUtils.contains(m_callback.getMessage(true).toString(), InfoMessageType.PRIVATE_CONFIG_CHANGE_ONLY_ALLOWED_FOR_OWN_USER.getValue()));
		m_callback.reinit();

		m_client.disconnect();
	}
	
	@Test(expected=MqttException.class)
	public void badPassword() throws Exception {
		SSLSocketFactory ssf = configureSSLSocketFactory();

		MqttConnectOptions options = new MqttConnectOptions();
		options.setUserName(USERNAME);
		options.setPassword(services.getHashing().generateHash(OTHER_USERNAME).toCharArray());
		options.setSocketFactory(ssf);
		m_client.connect(options);
	}
}
