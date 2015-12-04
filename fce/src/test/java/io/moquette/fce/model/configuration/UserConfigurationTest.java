package io.moquette.fce.model.configuration;

import static org.junit.Assert.*;

import org.junit.Test;

import io.moquette.fce.model.configuration.FceAction;
import io.moquette.fce.model.configuration.UserConfiguration;
import io.moquette.plugin.MqttAction;

public class UserConfigurationTest {

	private static final String ID = "testidentifier";
	private static final String USER = "testuser";
	
	@Test
	public void testValidationForAction() {
		UserConfiguration userConfig = new UserConfiguration(USER, ID, FceAction.PUBLISH, null, null, null, null);
		assertFalse(userConfig.isValid(null, null, MqttAction.SUBSCRIBE));
	}

}
