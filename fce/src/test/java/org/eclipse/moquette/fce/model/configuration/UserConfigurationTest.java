package org.eclipse.moquette.fce.model.configuration;

import static org.junit.Assert.*;

import org.eclipse.moquette.plugin.MqttAction;
import org.junit.Test;

public class UserConfigurationTest {

	private static final String ID = "testidentifier";
	private static final String USER = "testuser";
	
	@Test
	public void testValidationForAction() {
		UserConfiguration userConfig = new UserConfiguration(USER, ID, FceAction.PUBLISH, null, null, null, null);
		assertFalse(userConfig.isValid(null, null, MqttAction.SUBSCRIBE));
	}

}
