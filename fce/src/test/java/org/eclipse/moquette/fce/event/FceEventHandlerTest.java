package org.eclipse.moquette.fce.event;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

import org.eclipse.moquette.fce.context.FceContext;
import org.eclipse.moquette.fce.context.HashAssignmentStore;
import org.eclipse.moquette.fce.service.FceServiceFactory;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.junit.Test;

public class FceEventHandlerTest {

	private static final String PLUGIN_USER_HASH = "test";
	
	@Test
	public void testWhenPluginClient() {
		FceServiceFactory sf = mock(FceServiceFactory.class);
		FceContext context = mock(FceContext.class);
		when(context.getPluginIdentifier()).thenReturn(PLUGIN_USER_HASH);
		when(context.getHashAssignment()).thenReturn(mock(HashAssignmentStore.class));
		when(context.getHashAssignment().get(any(String.class))).thenReturn(PLUGIN_USER_HASH);
		ManagedStoreHandler handler = new ManagedStoreHandler(context, sf);
		AuthorizationProperties authPropsPlugin = new AuthorizationProperties(null, null, PLUGIN_USER_HASH, false, null);
		assertTrue(handler.canDoOperation(authPropsPlugin, null));
	}
	
	@Test
	public void testWhenAnonymous() {
		FceServiceFactory sf = mock(FceServiceFactory.class);
		FceContext context = mock(FceContext.class);
		when(context.getHashAssignment()).thenReturn(mock(HashAssignmentStore.class));
		when(context.getHashAssignment().get(any(String.class))).thenReturn(null);
		ManagedTopicHandler handler = new ManagedTopicHandler(context, sf);
		AuthorizationProperties authProps = new AuthorizationProperties(null, null, null, true, null);
		assertFalse(handler.canDoOperation(authProps, null));
	}

}
