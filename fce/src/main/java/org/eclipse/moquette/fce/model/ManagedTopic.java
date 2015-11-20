package org.eclipse.moquette.fce.model;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.moquette.fce.common.FceHashUtil;
import org.eclipse.moquette.fce.common.ManagedZone;
import org.eclipse.moquette.fce.common.ManagedZoneUtil;
import org.eclipse.moquette.fce.exception.FceSystemFailureException;
import org.eclipse.moquette.plugin.AuthorizationProperties;

public class ManagedTopic {

	public static final String USER_PREFIX = "/_";
	public static final String LEVEL_CHAR = "/";
	public static final String EVERYONE_TOPIC = "/_all";

	private final String topicIdentifer;

	public ManagedTopic(String topicIdentifer) {
		super();
		if (!topicIdentifer.startsWith(LEVEL_CHAR)) {
			throw new FceSystemFailureException("invalid topicfilter which doesn't start with /");
		}
		this.topicIdentifer = topicIdentifer;
	}

	public String getTopicIdentifer() {
		return topicIdentifer;
	}

	public String getTopicIdentifier(ManagedZone managedZone) {
		return ManagedZoneUtil.moveTopicIdentifierToZone(topicIdentifer, managedZone);
	}

	public String getUserTopicIdentifier(ManagedInformation managedInfo, ManagedZone managedZone) {
		return ManagedZoneUtil.moveTopicIdentifierToZone(
				topicIdentifer + USER_PREFIX + FceHashUtil.getFceHash(managedInfo), managedZone);
	}

	public String getUserTopicIdentifier(AuthorizationProperties authProps, ManagedZone managedZone) {
		return ManagedZoneUtil.moveTopicIdentifierToZone(topicIdentifer + USER_PREFIX + authProps.getClientId(),
				managedZone);
	}
	
	public String getEveryoneTopicIdentifier(ManagedZone managedZone){
		return ManagedZoneUtil.moveTopicIdentifierToZone(topicIdentifer+EVERYONE_TOPIC, managedZone);
	}

	public int getHierarchyDeep(ManagedZone managedZone){
		return StringUtils.countMatches(getTopicIdentifier(managedZone), LEVEL_CHAR);
	}
	
	@Override
	public String toString() {
		return topicIdentifer;
	}
}
