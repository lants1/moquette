package org.eclipse.moquette.fce.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.moquette.fce.common.ManagedZone;
import org.eclipse.moquette.fce.common.ManagedZoneUtil;
import org.eclipse.moquette.fce.exception.FceNoAuthorizationPossibleException;
import org.eclipse.moquette.fce.model.configuration.ManagedPermission;
import org.eclipse.moquette.fce.model.configuration.UserConfiguration;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.junit.Test;

public class ConfigurationDbServiceTest {

	private static final String TESTIDENTIFIER = "testidentifier";
	private static final String TESTUSER = "testuser";
	private static final String TOPIC_SAMPLE = "/house/firstfloor/light";
	private static final String TOPIC_SAMPLE_CONFIG_IN_SUBTOPIC = "/house/firstfloor/light/morelight";
	private static final String TOPIC_INVALID = "/asdf/asdfasfd/dfasf";

	@Test
	public void testPutGet() throws FceNoAuthorizationPossibleException {
		UserConfiguration userConfig = new UserConfiguration(TESTUSER, TESTIDENTIFIER, ManagedPermission.EVERYONE, null,
				null, null);
		
		AuthorizationProperties props = new AuthorizationProperties(null, null, TESTIDENTIFIER, null);
		
		ConfigurationDbService configService = new ConfigurationDbService(null);
		configService.put(TOPIC_SAMPLE, userConfig);

		assertTrue(configService
				.getConfigurationForSingleManagedTopic(
						ManagedZoneUtil.moveTopicIdentifierToZone(TOPIC_SAMPLE, ManagedZone.MANAGED_CONFIGURATION),props)
				.getUserIdentifier() == TESTIDENTIFIER);
		
		assertTrue(configService
				.getConfigurationForSingleManagedTopic(
						ManagedZoneUtil.moveTopicIdentifierToZone(TOPIC_SAMPLE_CONFIG_IN_SUBTOPIC, ManagedZone.MANAGED_CONFIGURATION), props)
				.getUserIdentifier() == TESTIDENTIFIER);
	}

	@Test(expected = FceNoAuthorizationPossibleException.class)
	public void testPutGetException() throws FceNoAuthorizationPossibleException {
		UserConfiguration userConfig = new UserConfiguration(TESTUSER, TESTIDENTIFIER, ManagedPermission.EVERYONE, null,
				null, null);
		ConfigurationDbService configService = new ConfigurationDbService(null);
		configService.put(TOPIC_SAMPLE, userConfig);

		assertTrue(configService.getConfigurationForSingleManagedTopic(TOPIC_INVALID)
				.getUserIdentifier() == TESTIDENTIFIER);
	}

	@Test
	public void testIsManaged() throws FceNoAuthorizationPossibleException {
		UserConfiguration userConfig = new UserConfiguration(TESTUSER, TESTIDENTIFIER, ManagedPermission.EVERYONE, null,
				null, null);
		ConfigurationDbService configService = new ConfigurationDbService(null);
		configService.put(TOPIC_SAMPLE, userConfig);

		assertTrue(configService.isTopicFilterManaged(TOPIC_SAMPLE));
		assertFalse(configService.isTopicFilterManaged(TOPIC_INVALID));
	}

}
