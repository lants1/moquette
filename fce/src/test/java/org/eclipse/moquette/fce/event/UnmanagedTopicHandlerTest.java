package org.eclipse.moquette.fce.event;

import static org.junit.Assert.*;

import org.junit.Test;

public class UnmanagedTopicHandlerTest {

	@Test
	public void testDoOperation() {
		UnmanagedTopicHandler unmanagedHandler = new UnmanagedTopicHandler(null, null);
		assertTrue(unmanagedHandler.canDoOperation(null, null));
	}

}
