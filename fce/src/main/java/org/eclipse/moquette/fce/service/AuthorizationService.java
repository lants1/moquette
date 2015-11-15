package org.eclipse.moquette.fce.service;

import java.util.EnumSet;
import java.util.Set;

import org.eclipse.moquette.fce.common.ManagedZone;
import org.eclipse.moquette.fce.common.TopicPermission;

public class AuthorizationService {

	public TopicPermission getBasicPermission(String topicFilter){
		
		Set<ManagedZone> managedZones = EnumSet.allOf(ManagedZone.class);
		for(ManagedZone managedZone : managedZones) {
		    if(topicFilter.startsWith(managedZone.getTopicPrefix())){
		    	return managedZone.getPermission();
		    }
		}
		
		return TopicPermission.READ_WRITE;
	}
}
