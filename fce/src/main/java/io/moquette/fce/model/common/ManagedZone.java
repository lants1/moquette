package io.moquette.fce.model.common;

/**
 * ManagedZone enum which defines all available ManagedZones.
 * 
 * @author lants1
 *
 */
public enum ManagedZone {

	CONFIG_GLOBAL("/$MANAGED/CONFIGURATION/GLOBAL", TopicPermission.READ, ManagedScope.GLOBAL),
	CONFIG_PRIVATE("/$MANAGED/CONFIGURATION/PRIVATE", TopicPermission.READ, ManagedScope.PRIVATE),
	QUOTA_GLOBAL("/$MANAGED/QUOTA/GLOBAL", TopicPermission.READ, ManagedScope.GLOBAL),
	QUOTA_PRIVATE("/$MANAGED/QUOTA/PRIVATE", TopicPermission.READ, ManagedScope.PRIVATE),
	INFO("/$MANAGED/INFO", TopicPermission.READ, ManagedScope.GLOBAL),
	INTENT("/$MANAGED/INTENT", TopicPermission.WRITE, ManagedScope.GLOBAL);

	private final String topicPrefix;
	private final TopicPermission basePerm;
	private final ManagedScope scope;
	
	private ManagedZone(String topicPrefix, TopicPermission basePerm, ManagedScope scope) {
		this.topicPrefix = topicPrefix;
		this.basePerm = basePerm;
		this.scope = scope;
	}

	/**
	 * Gets the topic prefix of the managed zone.
	 * 
	 * @return String /topicPrefixForManagedZone
	 */
	public String getTopicPrefix() {
		return topicPrefix;
	}
	
	/**
	 * Gets the topic filter of the managed zone.
	 * 
	 * @return String /topicPrefixForManagedZone/#
	 */
	public String getTopicFilter() {
		return topicPrefix+"/#";
	}
	
	/**
	 * Gets the TopicPermission of the managed zone.
	 * 
	 * @return TopicPermission for ManagedZone
	 */
	public TopicPermission getPermission(){
		return basePerm;
	}
	
	/**
	 * Gets the ManagedScope of the Managed Zone.
	 * 
	 * @return ManagedScope for ManagedZone
	 */
	public ManagedScope getScope(){
		return scope;
	}
}