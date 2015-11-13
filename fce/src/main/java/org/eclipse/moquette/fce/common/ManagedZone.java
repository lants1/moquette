package org.eclipse.moquette.fce.common;

public enum ManagedZone {

	MANAGED_CONFIGURATION("$MANAGED_CONFIGURATION", TopicPermission.READ),
	MANAGED_QUOTA("$MANAGED_QUOTA", TopicPermission.READ),
	MANAGED_INFO("$MANAGED_INFO", TopicPermission.READ),
	MANAGED_INTENT("$MANAGED_INTENT", TopicPermission.WRITE);

	private String topicPrefix;
	private TopicPermission basePerm;
	
	private ManagedZone(String topicPrefix, TopicPermission basePerm) {
		this.topicPrefix = topicPrefix;
		this.basePerm = basePerm;
	}

	public String getTopicPrefix() {
		return (topicPrefix);
	}
	
	public String getTopicFilter() {
		return (topicPrefix+"/#");
	}
	
	public TopicPermission getPermission(){
		return basePerm;
	}
	
}