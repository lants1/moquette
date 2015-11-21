package org.eclipse.moquette.fce.common;

import static org.junit.Assert.*;

import org.apache.commons.codec.binary.StringUtils;
import org.junit.Test;

public class ManagedZoneUtilTest {

	@Test
	public void getZoneForTopic() {
		assertTrue(ManagedZone.QUOTA == ManagedZoneUtil
				.getZoneForTopic(ManagedZone.QUOTA.getTopicPrefix() + "/asdfasf/asfas"));
	}

	@Test
	public void moveTopicIdentifierToZone() {
		assertTrue(StringUtils.equals(ManagedZoneUtil.moveTopicToZone(ManagedZone.QUOTA.getTopicPrefix() + "/asdf",
				ManagedZone.CONFIGURATION), ManagedZone.CONFIGURATION.getTopicPrefix() + "/asdf"));

		assertTrue(StringUtils.equals(ManagedZoneUtil.moveTopicToZone("/asdf", ManagedZone.CONFIGURATION),
				ManagedZone.CONFIGURATION.getTopicPrefix() + "/asdf"));
	}
}
