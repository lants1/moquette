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

	private static final String TESTIDENTIFIER = "testidentifier";
	private static final String TESTUSER = "testuser";
	
	private static final String ALLIDENTIFIER = "allidentifier";
	private static final String ALLUSER = "alluser";
	
	private static final ManagedTopic TOPIC_SAMPLE = new ManagedTopic("/house/firstfloor/light");
	private static final ManagedTopic TOPIC_SAMPLE_CONFIG_IN_SUBTOPIC = new ManagedTopic("/house/firstfloor/light/morelight");
	private static final ManagedTopic TOPIC_INVALID = new ManagedTopic("/asdf/asdfasfd/dfasf");

	@Test
	public void testPutGet() throws FceNoAuthorizationPossibleException {
		UserConfiguration userConfig = new UserConfiguration(TESTUSER, TESTIDENTIFIER, ManagedPermission.EVERYONE, null,
				null, null);

		AuthorizationProperties props = new AuthorizationProperties(null, null, TESTIDENTIFIER, null, null);

		ConfigurationDbService configService = new ConfigurationDbService(null);
		configService.put(TOPIC_SAMPLE.getUserTopicIdentifier(props, ManagedZone.MANAGED_CONFIGURATION), userConfig);

		assertTrue(configService.getConfigurationForSingleManagedTopic(TOPIC_SAMPLE, props)
				.getUserIdentifier() == TESTIDENTIFIER);

		assertTrue(configService.getConfigurationForSingleManagedTopic(TOPIC_SAMPLE_CONFIG_IN_SUBTOPIC, props)
				.getUserIdentifier() == TESTIDENTIFIER);
	}

	@Test(expected = FceNoAuthorizationPossibleException.class)
	public void testPutGetException() throws FceNoAuthorizationPossibleException {
		UserConfiguration userConfig = new UserConfiguration(TESTUSER, TESTIDENTIFIER, ManagedPermission.EVERYONE, null,
				null, null);
		
		AuthorizationProperties props = new AuthorizationProperties(null, null, TESTIDENTIFIER, null, null);

		ConfigurationDbService configService = new ConfigurationDbService(null);
		configService.put(TOPIC_SAMPLE.getUserTopicIdentifier(props, ManagedZone.MANAGED_CONFIGURATION), userConfig);


		assertTrue(configService.getConfigurationForSingleManagedTopic(TOPIC_INVALID, props)
				.getUserIdentifier() == TESTIDENTIFIER);
	}

	@Test
	public void testIsManaged() throws FceNoAuthorizationPossibleException {
		UserConfiguration userConfig = new UserConfiguration(TESTUSER, TESTIDENTIFIER, ManagedPermission.EVERYONE, null,
				null, null);
		AuthorizationProperties props = new AuthorizationProperties(null, null, TESTIDENTIFIER, null, null);

		ConfigurationDbService configService = new ConfigurationDbService(null);
		configService.put(TOPIC_SAMPLE.getUserTopicIdentifier(props, ManagedZone.MANAGED_CONFIGURATION), userConfig);

		assertTrue(configService.isTopicFilterManaged(TOPIC_SAMPLE));
		assertFalse(configService.isTopicFilterManaged(TOPIC_INVALID));
	}

	@Test
	public void testPutGetEveryone() throws FceNoAuthorizationPossibleException {
		UserConfiguration userConfig = new UserConfiguration(TESTUSER, TESTIDENTIFIER, ManagedPermission.EVERYONE, null,
				null, null);
		
		UserConfiguration everyoneConfig = new UserConfiguration(ALLUSER, ALLIDENTIFIER, ManagedPermission.EVERYONE, null,
				null, null);

		AuthorizationProperties props = new AuthorizationProperties(null, null, TESTIDENTIFIER, null, null);

		ConfigurationDbService configService = new ConfigurationDbService(null);
		configService.put(TOPIC_SAMPLE.getEveryoneTopicIdentifier(ManagedZone.MANAGED_CONFIGURATION), everyoneConfig);

		assertTrue(configService.getConfigurationForSingleManagedTopic(TOPIC_SAMPLE, props)
				.getUserIdentifier() == ALLIDENTIFIER);

		assertTrue(configService.getConfigurationForSingleManagedTopic(TOPIC_SAMPLE_CONFIG_IN_SUBTOPIC, props)
				.getUserIdentifier() == ALLIDENTIFIER);
		
		configService.put(TOPIC_SAMPLE.getUserTopicIdentifier(props, ManagedZone.MANAGED_CONFIGURATION), userConfig);

		assertTrue(configService.getConfigurationForSingleManagedTopic(TOPIC_SAMPLE, props)
				.getUserIdentifier() == TESTIDENTIFIER);

		assertTrue(configService.getConfigurationForSingleManagedTopic(TOPIC_SAMPLE_CONFIG_IN_SUBTOPIC, props)
				.getUserIdentifier() == TESTIDENTIFIER);
	}
}
