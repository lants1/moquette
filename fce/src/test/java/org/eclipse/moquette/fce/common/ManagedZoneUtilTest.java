package org.eclipse.moquette.fce.common;

import static org.junit.Assert.*;

import org.apache.commons.codec.binary.StringUtils;
import org.junit.Test;

public class ManagedZoneUtilTest {

	@Test
	public void getZoneForTopic() {
		assertTrue(ManagedZone.MANAGED_QUOTA == ManagedZoneUtil
				.getZoneForTopic(ManagedZone.MANAGED_QUOTA.getTopicPrefix() + "/asdfasf/asfas"));
	}

	@Test
	public void moveTopicIdentifierToZone() {
		assertTrue(StringUtils.equals(
				ManagedZoneUtil.moveTopicToZone(ManagedZone.MANAGED_QUOTA.getTopicPrefix() + "/asdf",
						ManagedZone.MANAGED_CONFIGURATION),
				ManagedZone.MANAGED_CONFIGURATION.getTopicPrefix() + "/asdf"));
		
		
		assertTrue(StringUtils.equals(
				ManagedZoneUtil.moveTopicToZone("/asdf",
						ManagedZone.MANAGED_CONFIGURATION),
				ManagedZone.MANAGED_CONFIGURATION.getTopicPrefix() + "/asdf"));
	}
}
