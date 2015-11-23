package org.eclipse.moquette.fce.common;

import static org.junit.Assert.*;

import org.apache.commons.codec.binary.StringUtils;
import org.junit.Test;

public class ManagedZoneUtilTest {

	@Test
	public void getZoneForTopic() {
		assertTrue(ManagedZone.QUOTA_GLOBAL == ManagedZoneUtil
				.getZoneForTopic(ManagedZone.QUOTA_GLOBAL.getTopicPrefix() + "/asdfasf/asfas"));
	}

	@Test
	public void moveTopicIdentifierToZone() {
		assertTrue(StringUtils.equals(ManagedZoneUtil.moveTopicToZone(ManagedZone.QUOTA_GLOBAL.getTopicPrefix() + "/asdf",
				ManagedZone.CONFIGURATION_GLOBAL), ManagedZone.CONFIGURATION_GLOBAL.getTopicPrefix() + "/asdf"));

		assertTrue(StringUtils.equals(ManagedZoneUtil.moveTopicToZone("/asdf", ManagedZone.CONFIGURATION_GLOBAL),
				ManagedZone.CONFIGURATION_GLOBAL.getTopicPrefix() + "/asdf"));
	}
}
