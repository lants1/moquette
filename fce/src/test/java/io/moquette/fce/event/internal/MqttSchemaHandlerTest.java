package io.moquette.fce.event.internal;

import static org.junit.Assert.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.Test;

import io.moquette.fce.context.FceContext;
import io.moquette.fce.service.FceServiceFactory;
import io.moquette.plugin.IBrokerConfig;

public class MqttSchemaHandlerTest {

	private static final String TOPIC = "/test";

	@Test
	public void testSchemaMessageArrived() throws Exception {
		IBrokerConfig mock = mock(IBrokerConfig.class);
		when(mock.getProperty(any(String.class))).thenReturn("bla");
		FceContext context = new FceContext(mock, null);
		MqttSchemaHandler handler = new MqttSchemaHandler(context, new FceServiceFactory(context, null));
		MqttMessage msg = new MqttMessage();
		msg.setPayload("test".getBytes());
		handler.messageArrived(TOPIC, msg);
		assertTrue(handler.getContext().getSchemaAssignment().get(TOPIC).array().length > 0);
	}

}
