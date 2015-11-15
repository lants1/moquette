package org.eclipse.moquette.fce.common;

import java.util.EnumSet;
import java.util.Set;

public final class ManagedZoneUtil {

	public static ManagedZone getZoneForTopic(String topic){
		Set<ManagedZone> zones = EnumSet.allOf(ManagedZone.class);
		for(ManagedZone zone : zones) {
		    if(topic.startsWith(zone.getTopicPrefix())){
		    	return zone;
		    }
		}
		throw new RuntimeException("no managed zone for topic found");
	}
}
