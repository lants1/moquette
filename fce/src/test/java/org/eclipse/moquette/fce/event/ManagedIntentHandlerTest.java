package org.eclipse.moquette.fce.event;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

import static org.mockito.Mockito.any;

import org.eclipse.moquette.fce.common.FceServiceFactoryMockImpl;
import org.eclipse.moquette.fce.common.ManagedZone;
import org.eclipse.moquette.fce.exception.FceAuthorizationException;
import org.eclipse.moquette.fce.model.ManagedScope;
import org.eclipse.moquette.fce.model.ManagedTopic;
import org.eclipse.moquette.fce.model.configuration.AdminPermission;
import org.eclipse.moquette.fce.model.configuration.UserConfiguration;
import org.eclipse.moquette.fce.service.ConfigurationDbService;
import org.eclipse.moquette.fce.service.IFceServiceFactory;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.junit.Test;

public class ManagedIntentHandlerTest {

	private static final String USER_IDENTIFIER = "hashtest";

	public static Charset charset = Charset.forName("UTF-8");
	public static CharsetEncoder encoder = charset.newEncoder();
	public static CharsetDecoder decoder = charset.newDecoder();

	public static ByteBuffer str_to_bb(String msg) {
		try {
			return encoder.encode(CharBuffer.wrap(msg));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Test
	public void testUnmanaged() {
		ManagedIntentHandler handler = mock(ManagedIntentHandler.class);
		ConfigurationDbService configDbService = mock(ConfigurationDbService.class);
		when(configDbService.isManaged(any(ManagedTopic.class))).thenReturn(false);
		IFceServiceFactory services = new FceServiceFactoryMockImpl(null, configDbService, null, null, null);


		UserConfiguration userConfig = new UserConfiguration(null, null, null, null, null, null, null);
		
		AuthorizationProperties authPropsPlugin = new AuthorizationProperties(
				ManagedZone.CONFIG_GLOBAL.getTopicPrefix() + "/house/_" + USER_IDENTIFIER, null, null, false,
				str_to_bb(services.getJsonParser().serialize(userConfig)));
		when(handler.preCheckManagedZone(authPropsPlugin, null)).thenReturn(null);
		when(handler.getServices()).thenReturn(services);
		when(handler.canDoOperation(authPropsPlugin, null)).thenCallRealMethod();
		assertTrue(handler.canDoOperation(authPropsPlugin, null));
	}
	
	@Test
	public void testPrivate() {
		ManagedIntentHandler handler = mock(ManagedIntentHandler.class);
		ConfigurationDbService configDbService = mock(ConfigurationDbService.class);
		IFceServiceFactory services = new FceServiceFactoryMockImpl(null, configDbService, null, null, null);
		when(configDbService.isManaged(any(ManagedTopic.class))).thenReturn(true);

		UserConfiguration userConfig = new UserConfiguration(null, null, null, null, null, ManagedScope.PRIVATE, null);
		
		AuthorizationProperties authPropsPlugin = new AuthorizationProperties(
				ManagedZone.CONFIG_GLOBAL.getTopicPrefix() + "/house/_" + USER_IDENTIFIER, null, null, false,
				str_to_bb(services.getJsonParser().serialize(userConfig)));
		when(handler.preCheckManagedZone(authPropsPlugin, null)).thenReturn(null);
		when(handler.getServices()).thenReturn(services);
		when(handler.canDoOperation(authPropsPlugin, null)).thenCallRealMethod();
		assertTrue(handler.canDoOperation(authPropsPlugin, null));
	}


	@Test
	public void testUserConfigNull() {
		ManagedIntentHandler handler = mock(ManagedIntentHandler.class);
		ConfigurationDbService configDbService = mock(ConfigurationDbService.class);
		IFceServiceFactory services = new FceServiceFactoryMockImpl(null, configDbService, null, null, null);
		when(configDbService.isManaged(any(ManagedTopic.class))).thenReturn(true);

		UserConfiguration userConfig = new UserConfiguration(null, null, null, null, null, ManagedScope.GLOBAL, null);
		
		AuthorizationProperties authPropsPlugin = new AuthorizationProperties(
				ManagedZone.CONFIG_GLOBAL.getTopicPrefix() + "/house/_" + USER_IDENTIFIER, null, null, false,
				str_to_bb(services.getJsonParser().serialize(userConfig)));
		when(handler.preCheckManagedZone(authPropsPlugin, null)).thenReturn(null);
		when(handler.getServices()).thenReturn(services);
		when(handler.canDoOperation(authPropsPlugin, null)).thenCallRealMethod();
		assertFalse(handler.canDoOperation(authPropsPlugin, null));
	}
	
	
	@Test
	public void testUserConfigNone() throws FceAuthorizationException{
		ManagedIntentHandler handler = mock(ManagedIntentHandler.class);
		ConfigurationDbService configDbService = mock(ConfigurationDbService.class);
		IFceServiceFactory services = new FceServiceFactoryMockImpl(null, configDbService, null, null, null);
		when(configDbService.isManaged(any(ManagedTopic.class))).thenReturn(true);
		when(configDbService.getConfiguration(any(AuthorizationProperties.class))).thenReturn(new UserConfiguration(null, null, null, AdminPermission.NONE, null, null, null));

		UserConfiguration userConfig = new UserConfiguration(null, null, null, null, null, ManagedScope.GLOBAL, null);
		
		AuthorizationProperties authPropsPlugin = new AuthorizationProperties(
				ManagedZone.CONFIG_GLOBAL.getTopicPrefix() + "/house/_" + USER_IDENTIFIER, null, null, false,
				str_to_bb(services.getJsonParser().serialize(userConfig)));
		when(handler.preCheckManagedZone(authPropsPlugin, null)).thenReturn(null);
		when(handler.getServices()).thenReturn(services);
		when(handler.canDoOperation(authPropsPlugin, null)).thenCallRealMethod();
		assertFalse(handler.canDoOperation(authPropsPlugin, null));
	}
	
	@Test
	public void testUserConfigValidForUser() throws FceAuthorizationException{
		ManagedIntentHandler handler = mock(ManagedIntentHandler.class);
		ConfigurationDbService configDbService = mock(ConfigurationDbService.class);
		IFceServiceFactory services = new FceServiceFactoryMockImpl(null, configDbService, null, null, null);
		when(configDbService.isManaged(any(ManagedTopic.class))).thenReturn(true);
		when(configDbService.getConfiguration(any(AuthorizationProperties.class))).thenReturn(new UserConfiguration(null, null, null, AdminPermission.ALL, null, null, null));

		UserConfiguration userConfig = new UserConfiguration(null, null, null, null, null, ManagedScope.GLOBAL, null);
		
		AuthorizationProperties authPropsPlugin = new AuthorizationProperties(
				ManagedZone.CONFIG_GLOBAL.getTopicPrefix() + "/house/_" + USER_IDENTIFIER, null, null, false,
				str_to_bb(services.getJsonParser().serialize(userConfig)));
		when(handler.preCheckManagedZone(authPropsPlugin, null)).thenReturn(null);
		when(handler.getServices()).thenReturn(services);
		when(handler.canDoOperation(authPropsPlugin, null)).thenCallRealMethod();
		assertTrue(handler.canDoOperation(authPropsPlugin, null));
	}
}
