package org.eclipse.moquette.fce.event;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

import org.eclipse.moquette.fce.service.HashAssignmentService;
import org.eclipse.moquette.fce.service.IFceServiceFactory;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.junit.Test;

public class FceEventHandlerTest {

	private static final String PLUGIN_USER_HASH = "test";
	
	@Test
	public void testWhenPluginClient() {
		IFceServiceFactory sf = mock(IFceServiceFactory.class);
		when(sf.getHashAssignment()).thenReturn(mock(HashAssignmentService.class));
		when(sf.getHashAssignment().get(any(String.class))).thenReturn(PLUGIN_USER_HASH);
		ManagedStoreHandler handler = new ManagedStoreHandler(sf, PLUGIN_USER_HASH);
		AuthorizationProperties authPropsPlugin = new AuthorizationProperties(null, null, PLUGIN_USER_HASH, false, null);
		assertTrue(handler.canDoOperation(authPropsPlugin, null));
	}
	
	@Test
	public void testWhenAnonymous() {
		IFceServiceFactory sf = mock(IFceServiceFactory.class);
		when(sf.getHashAssignment()).thenReturn(mock(HashAssignmentService.class));
		when(sf.getHashAssignment().get(any(String.class))).thenReturn(null);
		ManagedTopicHandler handler = new ManagedTopicHandler(sf, null);
		AuthorizationProperties authProps = new AuthorizationProperties(null, null, null, true, null);
		assertFalse(handler.canDoOperation(authProps, null));
	}

}
