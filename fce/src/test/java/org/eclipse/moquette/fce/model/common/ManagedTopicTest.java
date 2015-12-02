package org.eclipse.moquette.fce.model.common;

import static org.junit.Assert.*;

import org.eclipse.moquette.fce.model.common.ManagedTopic;
import org.eclipse.moquette.fce.model.common.ManagedZone;
import org.junit.Test;

public class ManagedTopicTest {

	private static final String TESTIDENTIFIER = "TESTIDENTIFIER";
	private static final String SAMPLE1_HOUSE_LIGHT = "/sample1/house/light";
	private static final String SAMPLE2_HOUSE_LIGHT = ManagedZone.CONFIG_GLOBAL.getTopicPrefix() +"/sample1/house/light";

	@Test
	public void testEveryoneTopic() {
		ManagedTopic sample1 = new ManagedTopic(SAMPLE1_HOUSE_LIGHT);
		ManagedTopic sample2 = new ManagedTopic(SAMPLE2_HOUSE_LIGHT);
		assertTrue(sample1.getAllIdentifier(ManagedZone.QUOTA_GLOBAL).equals(ManagedZone.QUOTA_GLOBAL.getTopicPrefix()+SAMPLE1_HOUSE_LIGHT+ManagedTopic.ALL_TOPIC));
		assertTrue(sample2.getAllIdentifier(ManagedZone.QUOTA_GLOBAL).equals(ManagedZone.QUOTA_GLOBAL.getTopicPrefix()+SAMPLE1_HOUSE_LIGHT+ManagedTopic.ALL_TOPIC));
	}
	
	
	@Test
	public void testHierarchyDeep() {
		ManagedTopic sample1 = new ManagedTopic(SAMPLE1_HOUSE_LIGHT);
		ManagedTopic sample2 = new ManagedTopic(SAMPLE2_HOUSE_LIGHT);
		assertSame(sample1.getHierarchyDeep(ManagedZone.QUOTA_GLOBAL), 6);
		assertSame(sample2.getHierarchyDeep(ManagedZone.QUOTA_GLOBAL), 6);
	}
	
	@Test
	public void testGetUserTopicIdentifier() {
		ManagedTopic sample1 = new ManagedTopic(SAMPLE1_HOUSE_LIGHT);
		
		assertTrue(sample1.getIdentifier(TESTIDENTIFIER, ManagedZone.QUOTA_GLOBAL).equals(ManagedZone.QUOTA_GLOBAL.getTopicPrefix()+SAMPLE1_HOUSE_LIGHT+ManagedTopic.USER_PREFIX+TESTIDENTIFIER));
	}

}
