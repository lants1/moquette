package org.eclipse.moquette.fce.model.quota;

import static org.junit.Assert.*;

import org.junit.Test;

import io.moquette.fce.model.quota.TransmittedDataState;

public class TransmittedDataStateTest {

	@Test
	public void testFlush() {
		TransmittedDataState state = new TransmittedDataState(3,3);
		state.flush();
		assertTrue(state.getCurrentQuota() == 0);
		assertTrue(state.getMaxQuota() == 3);
	}
	
}
