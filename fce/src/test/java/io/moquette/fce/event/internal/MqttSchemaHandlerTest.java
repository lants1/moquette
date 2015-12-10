package io.moquette.fce.event.internal;

import static org.junit.Assert.*;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.Test;

import io.moquette.fce.context.FceContext;
import io.moquette.fce.service.FceServiceFactory;

public class MqttSchemaHandlerTest {

	private static final String TOPIC = "/test";

	@Test
	public void testSchemaMessageArrived() throws Exception {
		MqttSchemaHandler handler = new MqttSchemaHandler(new FceContext(null, null), new FceServiceFactory(null, null));
		MqttMessage msg = new MqttMessage();
		msg.setPayload("test".getBytes());
		handler.messageArrived(TOPIC, msg);
		handler.getContext();
		assertFalse(handler.getContext().getHashAssignment().get(TOPIC).isEmpty());
	}

}
