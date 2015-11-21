package org.eclipse.moquette.fce.model;

import static org.junit.Assert.*;

import org.eclipse.moquette.fce.common.ManagedZone;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.junit.Test;

public class ManagedTopicTest {

	private static final String TESTIDENTIFIER = "TESTIDENTIFIER";
	private static final String SAMPLE1_HOUSE_LIGHT = "/sample1/house/light";
	private static final String SAMPLE2_HOUSE_LIGHT = ManagedZone.CONFIGURATION.getTopicPrefix() +"/sample1/house/light";

	@Test
	public void testEveryoneTopic() {
		ManagedTopic sample1 = new ManagedTopic(SAMPLE1_HOUSE_LIGHT);
		ManagedTopic sample2 = new ManagedTopic(SAMPLE2_HOUSE_LIGHT);
		assertTrue(sample1.getAllIdentifier(ManagedZone.QUOTA).equals(ManagedZone.QUOTA.getTopicPrefix()+SAMPLE1_HOUSE_LIGHT+ManagedTopic.ALL_TOPIC));
		assertTrue(sample2.getAllIdentifier(ManagedZone.QUOTA).equals(ManagedZone.QUOTA.getTopicPrefix()+SAMPLE1_HOUSE_LIGHT+ManagedTopic.ALL_TOPIC));
	}
	
	
	@Test
	public void testHierarchyDeep() {
		ManagedTopic sample1 = new ManagedTopic(SAMPLE1_HOUSE_LIGHT);
		ManagedTopic sample2 = new ManagedTopic(SAMPLE2_HOUSE_LIGHT);
		assertSame(sample1.getHierarchyDeep(ManagedZone.QUOTA), 4);
		assertSame(sample2.getHierarchyDeep(ManagedZone.QUOTA), 4);
	}
	
	@Test
	public void testGetUserTopicIdentifier() {
		ManagedTopic sample1 = new ManagedTopic(SAMPLE1_HOUSE_LIGHT);
		
		AuthorizationProperties authProps = new AuthorizationProperties(null, null, TESTIDENTIFIER, null, null);
		assertTrue(sample1.getIdentifier(authProps, ManagedZone.QUOTA).equals(ManagedZone.QUOTA.getTopicPrefix()+SAMPLE1_HOUSE_LIGHT+ManagedTopic.USER_PREFIX+TESTIDENTIFIER));
	}

}
