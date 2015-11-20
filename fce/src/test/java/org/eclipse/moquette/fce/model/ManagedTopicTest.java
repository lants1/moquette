package org.eclipse.moquette.fce.model;

import static org.junit.Assert.*;

import org.eclipse.moquette.fce.common.ManagedZone;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.junit.Test;

public class ManagedTopicTest {

	private static final String TESTIDENTIFIER = "TESTIDENTIFIER";
	private static final String SAMPLE1_HOUSE_LIGHT = "/sample1/house/light";
	private static final String SAMPLE2_HOUSE_LIGHT = ManagedZone.MANAGED_CONFIGURATION.getTopicPrefix() +"/sample1/house/light";

	@Test
	public void testEveryoneTopic() {
		ManagedTopic sample1 = new ManagedTopic(SAMPLE1_HOUSE_LIGHT);
		ManagedTopic sample2 = new ManagedTopic(SAMPLE2_HOUSE_LIGHT);
		assertTrue(sample1.getEveryoneTopicIdentifier(ManagedZone.MANAGED_QUOTA).equals(ManagedZone.MANAGED_QUOTA.getTopicPrefix()+SAMPLE1_HOUSE_LIGHT+ManagedTopic.EVERYONE_TOPIC));
		assertTrue(sample2.getEveryoneTopicIdentifier(ManagedZone.MANAGED_QUOTA).equals(ManagedZone.MANAGED_QUOTA.getTopicPrefix()+SAMPLE1_HOUSE_LIGHT+ManagedTopic.EVERYONE_TOPIC));
	}
	
	
	@Test
	public void testHierarchyDeep() {
		ManagedTopic sample1 = new ManagedTopic(SAMPLE1_HOUSE_LIGHT);
		ManagedTopic sample2 = new ManagedTopic(SAMPLE2_HOUSE_LIGHT);
		assertSame(sample1.getHierarchyDeep(ManagedZone.MANAGED_QUOTA), 4);
		assertSame(sample2.getHierarchyDeep(ManagedZone.MANAGED_QUOTA), 4);
	}
	
	@Test
	public void testGetUserTopicIdentifier() {
		ManagedTopic sample1 = new ManagedTopic(SAMPLE1_HOUSE_LIGHT);
		
		AuthorizationProperties authProps = new AuthorizationProperties(null, null, TESTIDENTIFIER, null, null);
		assertTrue(sample1.getUserTopicIdentifier(authProps, ManagedZone.MANAGED_QUOTA).equals(ManagedZone.MANAGED_QUOTA.getTopicPrefix()+SAMPLE1_HOUSE_LIGHT+ManagedTopic.USER_PREFIX+TESTIDENTIFIER));
	}

}
