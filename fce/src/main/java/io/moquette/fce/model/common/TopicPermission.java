package io.moquette.fce.model.common;

import java.util.EnumSet;
import java.util.Set;

import io.moquette.plugin.MqttAction;

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

	/**
	 * Is Readable according to topic permission.
	 * 
	 * @return true if TopicPermission is readable
	 */
	public boolean isReadable() {
		return readable;
	}

	/**
	 * Is MqttAction allowed for topic permission.
	 * 
	 * @param action MqttAction
	 * @return true if MqttAction is allowed for topic permission.
	 */
	public boolean isAllowed(MqttAction action) {
		if (MqttAction.PUBLISH.equals(action)) {
			return writeable;
		}
		if (MqttAction.SUBSCRIBE.equals(action)) {
			return readable;
		}
		return false;
	}
	
	/**
	 * Get TopicPermission for topicFilter.
	 * 
	 * @param topicFilter String
	 * @return TopicPermission for topic Filter
	 */
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
