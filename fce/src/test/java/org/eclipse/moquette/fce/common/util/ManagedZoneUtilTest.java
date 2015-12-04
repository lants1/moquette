package org.eclipse.moquette.fce.common.util;

import static org.junit.Assert.*;

import org.apache.commons.codec.binary.StringUtils;
import org.junit.Test;

import io.moquette.fce.common.util.ManagedZoneUtil;
import io.moquette.fce.model.common.ManagedZone;

public class ManagedZoneUtilTest {

	@Test
	public void getZoneForTopic() {
		assertTrue(ManagedZone.QUOTA_GLOBAL == ManagedZoneUtil
				.getZoneForTopic(ManagedZone.QUOTA_GLOBAL.getTopicPrefix() + "/asdfasf/asfas"));
	}

	@Test
	public void moveTopicIdentifierToZone() {
		assertTrue(StringUtils.equals(ManagedZoneUtil.moveTopicToZone(ManagedZone.QUOTA_GLOBAL.getTopicPrefix() + "/asdf",
				ManagedZone.CONFIG_GLOBAL), ManagedZone.CONFIG_GLOBAL.getTopicPrefix() + "/asdf"));

		assertTrue(StringUtils.equals(ManagedZoneUtil.moveTopicToZone("/asdf", ManagedZone.CONFIG_GLOBAL),
				ManagedZone.CONFIG_GLOBAL.getTopicPrefix() + "/asdf"));
	}
}
