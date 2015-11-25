package org.eclipse.moquette.fce.common;

import java.util.EnumSet;
import java.util.Set;

import org.eclipse.moquette.plugin.MqttAction;

/**
 * The Broker itself can read/write everything. Client have restricted access on
 * broker internal zones. This Enum represents the permission for the restricted
 * client access.
 */
public enum TopicPermission {
	READ(true, false), WRITE(false, true), READ_WRITE(true, true);

	private boolean readable;
	private boolean writeable;

	private TopicPermission(boolean readable, boolean writeable) {
		this.readable = readable;
		this.writeable = writeable;
	}

	public boolean isReadable() {
		return readable;
	}

	public boolean isAllowed(MqttAction action) {
		if (MqttAction.PUBLISH.equals(action)) {
			return writeable;
		}
		if (MqttAction.SUBSCRIBE.equals(action)) {
			return readable;
		}
		return false;
	}
	
	public static TopicPermission getBasicPermission(String topicFilter){
		
		Set<ManagedZone> managedZones = EnumSet.allOf(ManagedZone.class);
		for(ManagedZone managedZone : managedZones) {
		    if(topicFilter.startsWith(managedZone.getTopicPrefix())){
		    	return managedZone.getPermission();
		    }
		}
		
		return TopicPermission.READ_WRITE;
	}
}
