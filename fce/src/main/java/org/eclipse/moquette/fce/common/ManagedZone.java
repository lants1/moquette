package org.eclipse.moquette.fce.common;

public enum ManagedZone {

	CONFIGURATION_GLOBAL("/$MANAGED/CONFIGURATION/GLOBAL", TopicPermission.READ),
	CONFIGURATION_PRIVATE("/$MANAGED/CONFIGURATION/PRIVATE", TopicPermission.READ),
	QUOTA_GLOBAL("/$MANAGED/QUOTA/GLOBAL", TopicPermission.READ),
	QUOTA_PRIVATE("/$MANAGED/QUOTA/PRIVATE", TopicPermission.READ),
	INFO("/$MANAGED_/INFO", TopicPermission.READ),
	INTENT("/$MANAGED/INTENT", TopicPermission.WRITE);

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