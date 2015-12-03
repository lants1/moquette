package org.eclipse.moquette.fce.model.quota;

import static org.junit.Assert.*;

import org.junit.Test;

public class MessageCountStateTest {

	@Test
	public void testFlush() {
		MessageCountState state = new MessageCountState(3,3);
		state.flush();
		assertTrue(state.getCurrentQuotaCount() == 0);
		assertTrue(state.getMaxQuotaCount() == 3);
	}
	
	@Test
	public void testValid() {
		MessageCountState state = new MessageCountState(3,3);
		assertFalse(state.isValid(null, null, null));
		
		state = new MessageCountState(3,2);
		assertTrue(state.isValid(null, null, null));
	}

	@Test
	public void testSubstract() {
		MessageCountState state = new MessageCountState(3,1);
		state.substractRequestFromQuota(null);
		assertTrue(state.getCurrentQuotaCount() == 2);
	}
}
