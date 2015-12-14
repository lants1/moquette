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
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.junit.Test;


/**
 * Integrationtests for basic fce loading and handling functionality...
 * 
 * @author lants1
 *
 */
public class ValidationIntegrationTest extends FceIntegrationTest {

	@Test
	public void checkSchemaValidationForJson() throws Exception {
		SSLSocketFactory ssf = configureSSLSocketFactory();

		MqttConnectOptions options = new MqttConnectOptions();
		options.setUserName(USERNAME);
		options.setPassword(services.getHashing().generateHash(USERNAME).toCharArray());
		options.setSocketFactory(ssf);
		m_client.connect(options);

		String topic = "/ValidationIntegrationTest/dfsfda";
		String schemaTopic = "/json/schema";
		String intentTopic = ManagedZone.INTENT.getTopicPrefix() + topic;
		String infoTopic = ManagedZone.INFO.getTopicPrefix() + topic + "/#";

		m_client.subscribe(topic, 0);
		m_client.subscribe(infoTopic, 0);
		m_client.subscribe(schemaTopic, 0);

		String configuration = ReadFileUtil.readFileString("/integration/json_schema_validation.json");
		String jsonSchema = ReadFileUtil.readFileString("/validation/json/fstab.json");
		String jsonGood = ReadFileUtil.readFileString("/validation/json/fstab-good.json");
		String jsonBad = ReadFileUtil.readFileString("/validation/json/fstab-bad.json");
		
		m_callback.reinit();
		m_client.publish(schemaTopic, jsonSchema.getBytes(), 1, true);
		assertTrue(
				StringUtils.contains(m_callback.getMessage(false).toString(), "http://json-schema.org/draft-04/schema"));
		m_callback.reinit();
		
		m_client.publish(intentTopic, configuration.getBytes(), 0, true);
		assertTrue(
				StringUtils.contains(m_callback.getMessage(false).toString(), InfoMessageType.TOPIC_CONFIGURATION_ACCEPTED.getValue()));
		m_callback.reinit();

		Thread.sleep(500);
		
		m_client.publish(topic, jsonGood.getBytes(), 0, true);
		assertTrue(StringUtils.contains(m_callback.getMessage(false).toString(), "/dev/sda1"));
		m_callback.reinit();
		
		m_client.publish(topic, jsonBad.getBytes(), 0, true);
		assertTrue(StringUtils.contains(m_callback.getMessage(false).toString(), InfoMessageType.GLOBAL_CONFIG_REJECTED.getValue()));
		m_callback.reinit();
	}
	
	@Test
	public void checkSchemaValidationForXml() throws Exception {
		SSLSocketFactory ssf = configureSSLSocketFactory();

		MqttConnectOptions options = new MqttConnectOptions();
		options.setUserName(USERNAME);
		options.setPassword(services.getHashing().generateHash(USERNAME).toCharArray());
		options.setSocketFactory(ssf);
		m_client.connect(options);

		String topic = "/ValidationIntegrationTest/simplemanageXml";
		String schemaTopic = "/xml/schema";
		String intentTopic = ManagedZone.INTENT.getTopicPrefix() + topic;
		String infoTopic = ManagedZone.INFO.getTopicPrefix() + topic + "/#";

		m_client.subscribe(topic, 0);
		m_client.subscribe(infoTopic, 0);
		m_client.subscribe(schemaTopic, 0);

		String configuration = ReadFileUtil.readFileString("/integration/xml_schema_validation.json");
		String xmlSchema = ReadFileUtil.readFileString("/validation/xml/sampleSchema.xml");
		String xmlGood = ReadFileUtil.readFileString("/validation/xml/valid.xml");
		String xmlBad = ReadFileUtil.readFileString("/validation/xml/invalid.xml");
		
		m_callback.reinit();
		m_client.publish(schemaTopic, xmlSchema.getBytes(), 1, true);
		assertTrue(
				StringUtils.contains(m_callback.getMessage(false).toString(), "</complexType>"));
		m_callback.reinit();
		
		m_client.publish(intentTopic, configuration.getBytes(), 0, true);
		assertTrue(
				StringUtils.contains(m_callback.getMessage(false).toString(), InfoMessageType.TOPIC_CONFIGURATION_ACCEPTED.getValue()));
		m_callback.reinit();

		Thread.sleep(500);
		
		m_client.publish(topic, xmlGood.getBytes(), 0, true);
		assertTrue(StringUtils.contains(m_callback.getMessage(false).toString(), "<title>"));
		m_callback.reinit();
		
		m_client.publish(topic, xmlBad.getBytes(), 0, true);
		assertTrue(StringUtils.contains(m_callback.getMessage(false).toString(), InfoMessageType.GLOBAL_CONFIG_REJECTED.getValue()));
		m_callback.reinit();
	}
}
