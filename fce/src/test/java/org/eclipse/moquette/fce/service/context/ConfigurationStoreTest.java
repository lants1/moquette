package org.eclipse.moquette.fce.service.context;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.moquette.fce.context.ConfigurationStore;
import org.eclipse.moquette.fce.exception.FceAuthorizationException;
import org.eclipse.moquette.fce.model.common.ManagedTopic;
import org.eclipse.moquette.fce.model.common.ManagedZone;
import org.eclipse.moquette.fce.model.configuration.FceAction;
import org.eclipse.moquette.fce.model.configuration.UserConfiguration;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.junit.Test;

public class ConfigurationStoreTest {

	private static final String USERHASH = "testidentifier";
	private static final String USER = "testuser";

	private static final String ALL_ID = "allidentifier";
	private static final String ALL_USER = "alluser";

	private static final ManagedTopic TOPIC = new ManagedTopic("/house/firstfloor/light");
	private static final ManagedTopic CONFIG_SUBTOPIC = new ManagedTopic("/house/firstfloor/light/morelight");
	private static final ManagedTopic TOPIC_INVALID = new ManagedTopic("/asdf/asdfasfd/dfasf");

	@Test
	public void testPutGet() throws FceAuthorizationException {
		UserConfiguration userConfig = new UserConfiguration(USER, USERHASH, FceAction.ALL, null, null, null, null);

		AuthorizationProperties props = new AuthorizationProperties(TOPIC.getIdentifer(), null, USERHASH, null, null);

		ConfigurationStore configService = new ConfigurationStore(null, ManagedZone.CONFIG_GLOBAL);
		configService.put(TOPIC.getIdentifier(USERHASH, ManagedZone.CONFIG_GLOBAL), userConfig);

		assertTrue(configService.getConfiguration(props.getTopic(), USERHASH).getUserHash() == USERHASH);

		props = new AuthorizationProperties(CONFIG_SUBTOPIC.getIdentifer(), null, USERHASH, null, null);

		assertTrue(configService.getConfiguration(props.getTopic(), USERHASH).getUserHash() == USERHASH);
	}

	@Test
	public void testIsManaged() throws FceAuthorizationException {
		UserConfiguration userConfig = new UserConfiguration(USER, USERHASH, FceAction.ALL, null, null, null, null);
		ConfigurationStore configService = new ConfigurationStore(null, ManagedZone.CONFIG_GLOBAL);
		configService.put(TOPIC.getIdentifier(USERHASH, ManagedZone.CONFIG_GLOBAL), userConfig);

		assertTrue(configService.isManaged(TOPIC));
		assertFalse(configService.isManaged(TOPIC_INVALID));
	}

	@Test
	public void testPutGetEveryone() throws FceAuthorizationException {
		UserConfiguration userConfig = new UserConfiguration(USER, USERHASH, FceAction.ALL, null, null, null, null);

		UserConfiguration everyoneConfig = new UserConfiguration(ALL_USER, ALL_ID, FceAction.ALL, null,
				null, null, null);

		AuthorizationProperties props = new AuthorizationProperties(TOPIC.getIdentifer(), null, USERHASH, null, null);

		ConfigurationStore configService = new ConfigurationStore(null, ManagedZone.CONFIG_GLOBAL);
		configService.put(TOPIC.getAllIdentifier(ManagedZone.CONFIG_GLOBAL), everyoneConfig);

		assertTrue(configService.getConfiguration(props.getTopic(), USERHASH).getUserHash() == ALL_ID);

		props = new AuthorizationProperties(CONFIG_SUBTOPIC.getIdentifer(), null, USERHASH, null, null);

		assertTrue(configService.getConfiguration(props.getTopic(), USERHASH).getUserHash() == ALL_ID);

		configService.put(TOPIC.getIdentifier(USERHASH, ManagedZone.CONFIG_GLOBAL), userConfig);

		props = new AuthorizationProperties(TOPIC.getIdentifer(), null, USERHASH, null, null);

		assertTrue(configService.getConfiguration(props.getTopic(), USERHASH).getUserHash() == USERHASH);

		props = new AuthorizationProperties(CONFIG_SUBTOPIC.getIdentifer(), null, USERHASH, null, null);

		assertTrue(configService.getConfiguration(props.getTopic(), USERHASH).getUserHash() == USERHASH);
	}
}
