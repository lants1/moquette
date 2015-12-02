package org.eclipse.moquette.fce.model.common;

import static org.junit.Assert.*;

import org.junit.Test;

public class TopicPermissionTest {

	@Test
	public void testGetBasicPermission() {
		assertTrue(TopicPermission.READ_WRITE == TopicPermission.getBasicPermission("/test/test"));
		assertTrue(TopicPermission.READ == TopicPermission.getBasicPermission(ManagedZone.QUOTA_GLOBAL.getTopicPrefix()+"/test/test"));
		assertTrue(TopicPermission.WRITE == TopicPermission.getBasicPermission(ManagedZone.INTENT.getTopicPrefix()+"/test/test"));
	}

}
