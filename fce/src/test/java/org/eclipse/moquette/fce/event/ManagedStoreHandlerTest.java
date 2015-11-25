package org.eclipse.moquette.fce.event;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.moquette.fce.common.ManagedZone;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.junit.Test;

public class ManagedStoreHandlerTest {

	private static final String USER_IDENTIFIER = "hashtest";

	@Test
	public void testUserValidationClient() {
		ManagedStoreHandler handler = mock(ManagedStoreHandler.class);
		AuthorizationProperties authPropsPlugin = new AuthorizationProperties(
				ManagedZone.CONFIG_GLOBAL.getTopicPrefix() + "/house/_" + USER_IDENTIFIER, null, USER_IDENTIFIER,
				false, null);
		when(handler.preCheckManagedZone(authPropsPlugin, null)).thenReturn(null);
		when(handler.canDoOperation(authPropsPlugin, null)).thenCallRealMethod();
		assertTrue(handler.canDoOperation(authPropsPlugin, null));
	}

}
