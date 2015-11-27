package org.eclipse.moquette.fce.event;

import static org.junit.Assert.*;

import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.junit.Test;

public class UnmanagedTopicHandlerTest {

	@Test
	public void testDoOperation() {
		UnmanagedTopicHandler unmanagedHandler = new UnmanagedTopicHandler(null, null);
		assertTrue(unmanagedHandler.canDoOperation(new AuthorizationProperties(null, null, null, null, null), null));
	}

}
