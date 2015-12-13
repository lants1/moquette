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
import org.eclipse.paho.client.mqttv3.MqttException;
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
