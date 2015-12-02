package org.eclipse.moquette.fce.event;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.moquette.fce.context.FceContext;
import org.eclipse.moquette.fce.context.HashAssignmentStore;
import org.eclipse.moquette.fce.model.common.ManagedTopic;
import org.eclipse.moquette.fce.model.common.ManagedZone;
import org.eclipse.moquette.fce.service.FceServiceFactory;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.junit.Test;

public class ManagedStoreHandlerTest {

	private static final String USER_HASH = "hashtest";

	@Test
	public void testUserValidationClient() {
		FceServiceFactory sf = mock(FceServiceFactory.class);
		FceContext context = mock(FceContext.class);
		when(context.getHashAssignment()).thenReturn(mock(HashAssignmentStore.class));
		when(context.getHashAssignment()).thenReturn(mock(HashAssignmentStore.class));
		when(context.getHashAssignment().get(any(String.class))).thenReturn(USER_HASH);
		
		ManagedStoreHandler handler = mock(ManagedStoreHandler.class);
		when(handler.getServices()).thenReturn(sf);
		when(handler.getContext()).thenReturn(context);
		AuthorizationProperties authPropsPlugin = new AuthorizationProperties(
				ManagedZone.CONFIG_GLOBAL.getTopicPrefix() + "/house"+ManagedTopic.USER_PREFIX + USER_HASH, null, USER_HASH,
				false, null);
		
		when(handler.preCheckManagedZone(authPropsPlugin, null)).thenReturn(null);
		when(handler.canDoOperation(authPropsPlugin, null)).thenCallRealMethod();
		assertTrue(handler.canDoOperation(authPropsPlugin, null));
	}

}
