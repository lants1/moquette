package org.eclipse.moquette.fce.common;

import java.util.EnumSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.moquette.fce.exception.FceSystemFailureException;

public final class ManagedZoneUtil {

	public static ManagedZone getZoneForTopic(String topic){
		Set<ManagedZone> zones = EnumSet.allOf(ManagedZone.class);
		for(ManagedZone zone : zones) {
		    if(topic.startsWith(zone.getTopicPrefix())){
		    	return zone;
		    }
		}
		throw new FceSystemFailureException("no managed zone for topic found");
	}
	
	public static String moveTopicToZone(String topic, ManagedZone topicZone){
		
		Set<ManagedZone> zones = EnumSet.allOf(ManagedZone.class);
		for(ManagedZone zone : zones) {
		    if(topic.startsWith(zone.getTopicPrefix())){
		    	return StringUtils.replace(topic, zone.getTopicPrefix(), topicZone.getTopicPrefix());
		    }
		}
		return topicZone.getTopicPrefix() + topic;
	}
}
