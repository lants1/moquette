package org.eclipse.moquette.fce.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.moquette.fce.common.ManagedZone;
import org.eclipse.moquette.fce.exception.FceNoAuthorizationPossibleException;
import org.eclipse.moquette.fce.model.ManagedTopic;
import org.eclipse.moquette.fce.model.configuration.ManagedPermission;
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
	public void testPutGet() throws FceNoAuthorizationPossibleException {
		UserConfiguration userConfig = new UserConfiguration(USER, ID, ManagedPermission.EVERYONE, null, null, null);

		AuthorizationProperties props = new AuthorizationProperties(TOPIC.getTopicIdentifer(), null, ID, null, null);

		ConfigurationDbService configService = new ConfigurationDbService(null);
		configService.put(TOPIC.getUserTopicIdentifier(props, ManagedZone.MANAGED_CONFIGURATION), userConfig);

		assertTrue(configService.getConfiguration(props).getUserIdentifier() == ID);

		props = new AuthorizationProperties(CONFIG_SUBTOPIC.getTopicIdentifer(), null, ID, null, null);

		assertTrue(configService.getConfiguration(props).getUserIdentifier() == ID);
	}

	@Test(expected = FceNoAuthorizationPossibleException.class)
	public void testPutGetException() throws FceNoAuthorizationPossibleException {
		UserConfiguration userConfig = new UserConfiguration(USER, ID, ManagedPermission.EVERYONE, null, null, null);

		AuthorizationProperties props = new AuthorizationProperties(TOPIC_INVALID.getTopicIdentifer(), null, ID, null,
				null);

		ConfigurationDbService configService = new ConfigurationDbService(null);
		configService.put(TOPIC.getUserTopicIdentifier(props, ManagedZone.MANAGED_CONFIGURATION), userConfig);

		assertTrue(configService.getConfiguration(props).getUserIdentifier() == ID);
	}

	@Test
	public void testIsManaged() throws FceNoAuthorizationPossibleException {
		UserConfiguration userConfig = new UserConfiguration(USER, ID, ManagedPermission.EVERYONE, null, null, null);
		AuthorizationProperties props = new AuthorizationProperties(null, null, ID, null, null);

		ConfigurationDbService configService = new ConfigurationDbService(null);
		configService.put(TOPIC.getUserTopicIdentifier(props, ManagedZone.MANAGED_CONFIGURATION), userConfig);

		assertTrue(configService.isTopicFilterManaged(TOPIC));
		assertFalse(configService.isTopicFilterManaged(TOPIC_INVALID));
	}

	@Test
	public void testPutGetEveryone() throws FceNoAuthorizationPossibleException {
		UserConfiguration userConfig = new UserConfiguration(USER, ID, ManagedPermission.EVERYONE, null, null, null);

		UserConfiguration everyoneConfig = new UserConfiguration(ALL_USER, ALL_ID, ManagedPermission.EVERYONE, null,
				null, null);

		AuthorizationProperties props = new AuthorizationProperties(TOPIC.getTopicIdentifer(), null, ID, null, null);

		ConfigurationDbService configService = new ConfigurationDbService(null);
		configService.put(TOPIC.getEveryoneTopicIdentifier(ManagedZone.MANAGED_CONFIGURATION), everyoneConfig);

		assertTrue(configService.getConfiguration(props).getUserIdentifier() == ALL_ID);

		props = new AuthorizationProperties(CONFIG_SUBTOPIC.getTopicIdentifer(), null, ID, null, null);

		assertTrue(configService.getConfiguration(props).getUserIdentifier() == ALL_ID);

		configService.put(TOPIC.getUserTopicIdentifier(props, ManagedZone.MANAGED_CONFIGURATION), userConfig);

		props = new AuthorizationProperties(TOPIC.getTopicIdentifer(), null, ID, null, null);

		assertTrue(configService.getConfiguration(props).getUserIdentifier() == ID);

		props = new AuthorizationProperties(CONFIG_SUBTOPIC.getTopicIdentifer(), null, ID, null, null);

		assertTrue(configService.getConfiguration(props).getUserIdentifier() == ID);
	}
}
