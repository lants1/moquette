package org.eclipse.moquette.fce.model.quota;

import static org.junit.Assert.*;

import org.eclipse.moquette.plugin.MqttAction;
import org.junit.Test;

public class UserQuotaTest {

	private static final String ID = "testidentifier";
	private static final String USER = "testuser";
	
	@Test
	public void testOnEmptyUserQuotaValidation() {
		UserQuota userQuota = new UserQuota(ID, USER, null, null);
		assertTrue(userQuota.isValid(null, MqttAction.SUBSCRIBE));
	}

}
