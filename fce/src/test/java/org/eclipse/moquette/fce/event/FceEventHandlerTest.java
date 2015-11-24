package org.eclipse.moquette.fce.event;

import static org.junit.Assert.*;

import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.junit.Test;

public class FceEventHandlerTest {

	private static final String PLUGIN_IDENTIFIER = "test";
	
	@Test
	public void testWhenPluginClient() {
		ManagedStoreHandler handler = new ManagedStoreHandler(null, PLUGIN_IDENTIFIER);
		AuthorizationProperties authPropsPlugin = new AuthorizationProperties(null, null, PLUGIN_IDENTIFIER, false, null);
		assertTrue(handler.canDoOperation(authPropsPlugin, null));
	}
	
	@Test
	public void testWhenAnonymous() {
		ManagedTopicHandler handler = new ManagedTopicHandler(null, null);
		AuthorizationProperties authProps = new AuthorizationProperties(null, null, null, true, null);
		assertFalse(handler.canDoOperation(authProps, null));
	}

}
