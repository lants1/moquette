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
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.junit.Test;

/**
 * 
 * Integration tests for private configuration functionality...
 * 
 * @author lants1
 *
 */
public class FceInheritanceIntegrationTest extends FceIntegrationTest {

	@Test
	public void checkGlobalConfig() throws Exception {
		SSLSocketFactory ssf = configureSSLSocketFactory();

		MqttConnectOptions options = new MqttConnectOptions();
		options.setUserName(USERNAME);
		options.setPassword(services.getHashing().generateHash(USERNAME).toCharArray());
		options.setSocketFactory(ssf);
		m_client.connect(options);

		String topic = "/FceInheritanceIntegrationTest/ewrasdfas";
		String subTopic = "/FceInheritanceIntegrationTest/ewrasdfas/bla";
		String intentTopic = ManagedZone.INTENT.getTopicPrefix() + topic;
		String infoTopic = ManagedZone.INFO.getTopicPrefix() + topic + "/#";

		m_client.subscribe(subTopic, 0);
		m_client.subscribe(infoTopic, 0);

		String inputJson = ReadFileUtil.readFileString("/integration/restriction_manage_global.json");

		m_callback.reinit();
		m_client.publish(intentTopic, inputJson.getBytes(), 0, true);
		assertTrue(
				StringUtils.contains(m_callback.getMessage(true).toString(), InfoMessageType.TOPIC_CONFIGURATION_ACCEPTED.getValue()));
		m_callback.reinit();

		m_client.publish(subTopic, SAMPLE_MESSAGE.getBytes(), 0, true);
		assertEquals(SAMPLE_MESSAGE, m_callback.getMessage(true).toString());
		m_callback.reinit();
		
		m_client.publish(subTopic, SAMPLE_MESSAGE.getBytes(), 0, true);
		assertEquals(SAMPLE_MESSAGE, m_callback.getMessage(true).toString());
		m_callback.reinit();
		
		m_client.publish(subTopic, SAMPLE_MESSAGE.getBytes(), 0, true);
		assertTrue(StringUtils.contains(m_callback.getMessage(true).toString(), InfoMessageType.GLOBAL_QUOTA_DEPLETED.getValue()));
		m_callback.reinit();

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

		String topic = "/FceInheritanceIntegrationTest/simplemanage2";
		String subTopic = "/FceInheritanceIntegrationTest/simplemanage2/bla";
		String intentTopic = ManagedZone.INTENT.getTopicPrefix() + topic;
		String infoTopic = ManagedZone.INFO.getTopicPrefix() + topic + "/#";

		m_client.subscribe(subTopic, 0);
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
		
		m_client.publish(subTopic, SAMPLE_MESSAGE.getBytes(), 0, true);
		assertEquals(SAMPLE_MESSAGE, m_callback.getMessage(true).toString());
		m_callback.reinit();
		
		m_client.publish(subTopic, SAMPLE_MESSAGE.getBytes(), 0, true);
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

		String topic = "/FceInheritanceIntegrationTest/simplemanage3";
		String subtopic = "/FceInheritanceIntegrationTest/simplemanage3/bla";
		String intentTopic = ManagedZone.INTENT.getTopicPrefix() + topic;
		String infoTopic = ManagedZone.INFO.getTopicPrefix() + topic + "/#";

		m_client.subscribe(subtopic, 0);
		m_client.subscribe(infoTopic, 0);

		String inputJsonGlobal = ReadFileUtil.readFileString("/integration/restriction_manage_global.json");
		String inputJsonPrivate = ReadFileUtil.readFileString("/integration/restriction_manage_private.json");
		
		m_callback.reinit();
		// global config
		m_client.publish(intentTopic, inputJsonGlobal.getBytes(), 0, true);
		assertTrue(
				StringUtils.contains(m_callback.getMessage(true).toString(), InfoMessageType.TOPIC_CONFIGURATION_ACCEPTED.getValue()));
		m_callback.reinit();

		// private config
		m_client.publish(intentTopic, inputJsonPrivate.getBytes(), 0, true);
		assertTrue(
				StringUtils.contains(m_callback.getMessage(true).toString(), InfoMessageType.TOPIC_CONFIGURATION_ACCEPTED.getValue()));
		m_callback.reinit();
		
		Thread.sleep(1000);
		
		// publish
		m_client.publish(subtopic, SAMPLE_MESSAGE.getBytes(), 0, true);
		assertEquals(SAMPLE_MESSAGE, m_callback.getMessage(true).toString());
		m_callback.reinit();
		
		// new global config
		m_client.publish(intentTopic, inputJsonGlobal.getBytes(), 0, true);
		assertTrue(
				StringUtils.contains(m_callback.getMessage(true).toString(), InfoMessageType.TOPIC_CONFIGURATION_ACCEPTED.getValue()));
		m_callback.reinit();

		Thread.sleep(1000);
		
		// publish
		m_client.publish(subtopic, SAMPLE_MESSAGE.getBytes(), 0, true);
		assertTrue(StringUtils.contains(m_callback.getMessage(true).toString(), InfoMessageType.PRIVATE_QUOTA_DEPLETED.getValue()));
		m_callback.reinit();

		// new private config
		m_client.publish(intentTopic, inputJsonPrivate.getBytes(), 0, true);
		assertTrue(
				StringUtils.contains(m_callback.getMessage(true).toString(), InfoMessageType.TOPIC_CONFIGURATION_ACCEPTED.getValue()));
		m_callback.reinit();
		
		Thread.sleep(1000);
		
		// publish
		m_client.publish(subtopic, SAMPLE_MESSAGE.getBytes(), 0, true);
		assertEquals(SAMPLE_MESSAGE, m_callback.getMessage(true).toString());
		m_callback.reinit();
		
		// new private config
		m_client.publish(intentTopic, inputJsonPrivate.getBytes(), 0, true);
		assertTrue(
				StringUtils.contains(m_callback.getMessage(true).toString(), InfoMessageType.TOPIC_CONFIGURATION_ACCEPTED.getValue()));
		m_callback.reinit();
		
		Thread.sleep(1000);
		
		// publish
		m_client.publish(subtopic, SAMPLE_MESSAGE.getBytes(), 0, true);
		assertEquals(SAMPLE_MESSAGE, m_callback.getMessage(true).toString());
		m_callback.reinit();
		
		// publish
		m_client.publish(subtopic, SAMPLE_MESSAGE.getBytes(), 0, true);
		assertTrue(StringUtils.contains(m_callback.getMessage(true).toString(), InfoMessageType.GLOBAL_QUOTA_DEPLETED.getValue()));
		m_callback.reinit();
		
		m_client.disconnect();
	}
}
