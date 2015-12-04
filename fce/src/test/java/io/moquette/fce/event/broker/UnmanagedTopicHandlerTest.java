package io.moquette.fce.event.broker;

import static org.junit.Assert.*;

import org.junit.Test;

import io.moquette.fce.event.broker.UnmanagedTopicHandler;
import io.moquette.plugin.AuthorizationProperties;

public class UnmanagedTopicHandlerTest {

	@Test
	public void testDoOperation() {
		UnmanagedTopicHandler unmanagedHandler = new UnmanagedTopicHandler(null, null);
		assertTrue(unmanagedHandler.canDoOperation(new AuthorizationProperties(null, null, null, null, null), null));
	}

}
