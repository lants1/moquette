package io.moquette.fce.event.broker;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

import static org.mockito.Matchers.any;

import org.junit.Ignore;
import org.junit.Test;

import io.moquette.fce.context.ConfigurationStore;
import io.moquette.fce.context.FceContext;
import io.moquette.fce.context.HashAssignmentStore;
import io.moquette.fce.event.broker.ManagedIntentHandler;
import io.moquette.fce.exception.FceAuthorizationException;
import io.moquette.fce.model.common.CheckResult;
import io.moquette.fce.model.common.ManagedScope;
import io.moquette.fce.model.common.ManagedTopic;
import io.moquette.fce.model.common.ManagedZone;
import io.moquette.fce.model.configuration.UserConfiguration;
import io.moquette.fce.service.FceServiceFactory;
import io.moquette.fce.service.mqtt.FceMqttClientWrapper;
import io.moquette.fce.service.parser.JsonParserService;
import io.moquette.plugin.AuthorizationProperties;

public class ManagedIntentHandlerTest {

	private static final String USER_HASH = "hashtest";

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

	private FceServiceFactory getServiceFactoryMock() {
		FceServiceFactory sf = mock(FceServiceFactory.class);
		FceContext context = mock(FceContext.class);
		when(context.getHashAssignment()).thenReturn(mock(HashAssignmentStore.class));
		when(sf.getJsonParser()).thenReturn(new JsonParserService());
		when(sf.getMqtt()).thenReturn(mock(FceMqttClientWrapper.class));
		return sf;
	}

	@Test
	public void testPrivate() {
		ManagedIntentHandler handler = mock(ManagedIntentHandler.class);
		ConfigurationStore configDbService = mock(ConfigurationStore.class);

		FceContext contextMock = mock(FceContext.class);
		FceServiceFactory sf = getServiceFactoryMock();
		when(contextMock.getConfigurationStore(any(ManagedScope.class))).thenReturn(configDbService);

		when(configDbService.isManaged(any(ManagedTopic.class))).thenReturn(true);
		when(contextMock.getHashAssignment()).thenReturn(mock(HashAssignmentStore.class));
		when(contextMock.getHashAssignment().get(any(String.class))).thenReturn(USER_HASH);
		UserConfiguration userConfig = new UserConfiguration(null, USER_HASH, null, null, ManagedScope.PRIVATE,
				null);

		AuthorizationProperties authPropsPlugin = new AuthorizationProperties(
				ManagedZone.CONFIG_GLOBAL.getTopicPrefix() + "/house/_" + USER_HASH, null, USER_HASH, false,
				str_to_bb(sf.getJsonParser().serialize(userConfig)));
		when(handler.preCheckManagedZone(authPropsPlugin, null)).thenReturn(CheckResult.NO_RESULT);
		when(handler.getServices()).thenReturn(sf);
		when(handler.getContext()).thenReturn(contextMock);
		when(handler.canDoOperation(authPropsPlugin, null)).thenCallRealMethod();
		assertTrue(handler.canDoOperation(authPropsPlugin, null));
	}

	@Ignore
	@Test
	public void testUserConfigNull() {
		ManagedIntentHandler handler = mock(ManagedIntentHandler.class);
		ConfigurationStore configDbService = mock(ConfigurationStore.class);
		FceServiceFactory sf = getServiceFactoryMock();
		FceContext contextMock = mock(FceContext.class);
		when(contextMock.getHashAssignment()).thenReturn(mock(HashAssignmentStore.class));
		when(contextMock.getConfigurationStore(any(ManagedScope.class))).thenReturn(configDbService);

		when(configDbService.isManaged(any(ManagedTopic.class))).thenReturn(true);

		UserConfiguration userConfig = new UserConfiguration(null, null, null, null, ManagedScope.GLOBAL, null);

		AuthorizationProperties authPropsPlugin = new AuthorizationProperties(
				ManagedZone.CONFIG_GLOBAL.getTopicPrefix() + "/house/_" + USER_HASH, null, null, false,
				str_to_bb(sf.getJsonParser().serialize(userConfig)));
		when(handler.preCheckManagedZone(authPropsPlugin, null)).thenReturn(CheckResult.NO_RESULT);
		when(handler.getServices()).thenReturn(sf);
		when(handler.getContext()).thenReturn(contextMock);
		when(handler.canDoOperation(authPropsPlugin, null)).thenCallRealMethod();
		assertFalse(handler.canDoOperation(authPropsPlugin, null));
	}

	@Ignore
	@Test
	public void testUserConfigNone() throws FceAuthorizationException {
		ManagedIntentHandler handler = mock(ManagedIntentHandler.class);
		ConfigurationStore configDbService = mock(ConfigurationStore.class);
		FceServiceFactory sf = getServiceFactoryMock();

		FceContext contextMock = mock(FceContext.class);
		when(contextMock.getHashAssignment()).thenReturn(mock(HashAssignmentStore.class));
		when(contextMock.getConfigurationStore(any(ManagedScope.class))).thenReturn(configDbService);

		when(configDbService.isManaged(any(ManagedTopic.class))).thenReturn(true);

		UserConfiguration userConfig = new UserConfiguration(null, null, null, null, ManagedScope.GLOBAL, null);

		AuthorizationProperties authPropsPlugin = new AuthorizationProperties(
				ManagedZone.CONFIG_GLOBAL.getTopicPrefix() + "/house/_" + USER_HASH, null, null, false,
				str_to_bb(sf.getJsonParser().serialize(userConfig)));
		when(handler.preCheckManagedZone(authPropsPlugin, null)).thenReturn(CheckResult.NO_RESULT);
		when(handler.getServices()).thenReturn(sf);
		when(handler.getContext()).thenReturn(contextMock);
		when(handler.canDoOperation(authPropsPlugin, null)).thenCallRealMethod();
		assertFalse(handler.canDoOperation(authPropsPlugin, null));
	}
}