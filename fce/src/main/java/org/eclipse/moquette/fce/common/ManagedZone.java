package org.eclipse.moquette.fce.common;

import org.eclipse.moquette.fce.model.ManagedScope;

public enum ManagedZone {

	CONFIGURATION_GLOBAL("/$MANAGED/CONFIGURATION/GLOBAL", TopicPermission.READ, ManagedScope.GLOBAL),
	CONFIGURATION_PRIVATE("/$MANAGED/CONFIGURATION/PRIVATE", TopicPermission.READ, ManagedScope.PRIVATE),
	QUOTA_GLOBAL("/$MANAGED/QUOTA/GLOBAL", TopicPermission.READ, ManagedScope.GLOBAL),
	QUOTA_PRIVATE("/$MANAGED/QUOTA/PRIVATE", TopicPermission.READ, ManagedScope.PRIVATE),
	INFO("/$MANAGED_/INFO", TopicPermission.READ, ManagedScope.GLOBAL),
	INTENT("/$MANAGED/INTENT", TopicPermission.WRITE, ManagedScope.GLOBAL);

	private final String topicPrefix;
	private final TopicPermission basePerm;
	private final ManagedScope scope;
	
	private ManagedZone(String topicPrefix, TopicPermission basePerm, ManagedScope scope) {
		this.topicPrefix = topicPrefix;
		this.basePerm = basePerm;
		this.scope = scope;
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
	
	public ManagedScope getScope(){
		return scope;
	}
}