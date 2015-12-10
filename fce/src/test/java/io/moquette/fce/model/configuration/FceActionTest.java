package io.moquette.fce.model.configuration;

import static org.junit.Assert.*;

import org.junit.Test;

import io.moquette.plugin.MqttAction;

public class FceActionTest {

	@Test
	public void testCanDoOperation() {
		assertTrue(FceAction.ALL.canDoOperation(MqttAction.SUBSCRIBE));
		assertTrue(FceAction.ALL.canDoOperation(MqttAction.PUBLISH));
		assertTrue(FceAction.SUBSCRIBE.canDoOperation(MqttAction.SUBSCRIBE));
		assertFalse(FceAction.SUBSCRIBE.canDoOperation(MqttAction.PUBLISH));
		assertFalse(FceAction.PUBLISH.canDoOperation(MqttAction.SUBSCRIBE));
	}

}
