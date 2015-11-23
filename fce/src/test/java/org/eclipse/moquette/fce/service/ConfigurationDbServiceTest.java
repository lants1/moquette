package org.eclipse.moquette.fce.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.moquette.fce.common.ManagedZone;
import org.eclipse.moquette.fce.exception.FceAuthorizationException;
import org.eclipse.moquette.fce.model.ManagedTopic;
import org.eclipse.moquette.fce.model.configuration.FceAction;
import org.eclipse.moquette.fce.model.configuration.UserConfiguration;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.junit.Test;

public class ConfigurationDbServiceTest {

	private static final String ID = "testidentifier";
	private static final String USER = "testuser";

	private static final String ALL_ID = "allidentifier";
	private static final String ALL_USER = "alluser";

	private static final ManagedTopic TOPIC = new ManagedTopic("/house/firstfloor/light");
	private static final ManagedTopic CONFIG_SUBTOPIC = new ManagedTopic("/house/firstfloor/light/morelight");
	private static final ManagedTopic TOPIC_INVALID = new ManagedTopic("/asdf/asdfasfd/dfasf");

	@Test
	public void testPutGet() throws FceAuthorizationException {
		UserConfiguration userConfig = new UserConfiguration(USER, ID, FceAction.ALL, null, null, null, null);

		AuthorizationProperties props = new AuthorizationProperties(TOPIC.getIdentifer(), null, ID, null, null);

		ConfigurationDbService configService = new ConfigurationDbService(null);
		configService.put(TOPIC.getIdentifier(props, ManagedZone.CONFIGURATION_GLOBAL), userConfig);

		assertTrue(configService.getConfiguration(props).getUserIdentifier() == ID);

		props = new AuthorizationProperties(CONFIG_SUBTOPIC.getIdentifer(), null, ID, null, null);

		assertTrue(configService.getConfiguration(props).getUserIdentifier() == ID);
	}

	@Test(expected = FceAuthorizationException.class)
	public void testPutGetException() throws FceAuthorizationException {
		UserConfiguration userConfig = new UserConfiguration(USER, ID, FceAction.ALL, null, null, null, null);

		AuthorizationProperties props = new AuthorizationProperties(TOPIC_INVALID.getIdentifer(), null, ID, null,
				null);

		ConfigurationDbService configService = new ConfigurationDbService(null);
		configService.put(TOPIC.getIdentifier(props, ManagedZone.CONFIGURATION_GLOBAL), userConfig);

		assertTrue(configService.getConfiguration(props).getUserIdentifier() == ID);
	}

	@Test
	public void testIsManaged() throws FceAuthorizationException {
		UserConfiguration userConfig = new UserConfiguration(USER, ID, FceAction.ALL, null, null, null, null);
		AuthorizationProperties props = new AuthorizationProperties(null, null, ID, null, null);

		ConfigurationDbService configService = new ConfigurationDbService(null);
		configService.put(TOPIC.getIdentifier(props, ManagedZone.CONFIGURATION_GLOBAL), userConfig);

		assertTrue(configService.isManaged(TOPIC));
		assertFalse(configService.isManaged(TOPIC_INVALID));
	}

	@Test
	public void testPutGetEveryone() throws FceAuthorizationException {
		UserConfiguration userConfig = new UserConfiguration(USER, ID, FceAction.ALL, null, null, null, null);

		UserConfiguration everyoneConfig = new UserConfiguration(ALL_USER, ALL_ID, FceAction.ALL, null,
				null, null, null);

		AuthorizationProperties props = new AuthorizationProperties(TOPIC.getIdentifer(), null, ID, null, null);

		ConfigurationDbService configService = new ConfigurationDbService(null);
		configService.put(TOPIC.getAllIdentifier(ManagedZone.CONFIGURATION_GLOBAL), everyoneConfig);

		assertTrue(configService.getConfiguration(props).getUserIdentifier() == ALL_ID);

		props = new AuthorizationProperties(CONFIG_SUBTOPIC.getIdentifer(), null, ID, null, null);

		assertTrue(configService.getConfiguration(props).getUserIdentifier() == ALL_ID);

		configService.put(TOPIC.getIdentifier(props, ManagedZone.CONFIGURATION_GLOBAL), userConfig);

		props = new AuthorizationProperties(TOPIC.getIdentifer(), null, ID, null, null);

		assertTrue(configService.getConfiguration(props).getUserIdentifier() == ID);

		props = new AuthorizationProperties(CONFIG_SUBTOPIC.getIdentifer(), null, ID, null, null);

		assertTrue(configService.getConfiguration(props).getUserIdentifier() == ID);
	}
}
