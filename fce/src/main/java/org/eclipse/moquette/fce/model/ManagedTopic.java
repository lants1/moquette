package org.eclipse.moquette.fce.model;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.moquette.fce.common.FceHashUtil;
import org.eclipse.moquette.fce.common.ManagedZone;
import org.eclipse.moquette.fce.common.ManagedZoneUtil;
import org.eclipse.moquette.fce.exception.FceSystemFailureException;
import org.eclipse.moquette.plugin.AuthorizationProperties;

public class ManagedTopic {

	private static final String USER_PREFIX = "/_";
	private static final String SINGLE_LEVEL = "/+";
	private static final String MULTI_LEVEL = "/#";
	private static final String LEVEL_CHAR = "/";
	private static final String EVERYONE_TOPIC = "/_all";

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

	public String getTopicIdentifierWithoutWildcards(ManagedZone managedZone) {
		return removeWildcards(getTopicIdentifier(managedZone));
	}

	public String getUserTopicIdentifierWithoutWildcards(ManagedInformation managedInfo, ManagedZone managedZone) {
		return removeWildcards(getUserTopicIdentifier(managedInfo, managedZone));
	}

	public String getUserTopicIdentifierWithoutWildcards(AuthorizationProperties authProps, ManagedZone managedZone) {
		return removeWildcards(getUserTopicIdentifier(authProps, managedZone));
	}
	
	public String getEveryoneTopicIdentifier(ManagedZone managedZone){
		return ManagedZoneUtil.moveTopicIdentifierToZone(removeWildcards(topicIdentifer)+EVERYONE_TOPIC, managedZone);
	}
	
	private String removeWildcards(String stringToRemoveWildcards){
		return StringUtils.removeEnd(
				StringUtils.removeEnd(stringToRemoveWildcards, SINGLE_LEVEL), MULTI_LEVEL);
	}

	public boolean isSingleTopic() {
		return !StringUtils.endsWithAny(topicIdentifer, SINGLE_LEVEL, MULTI_LEVEL);
	}

	public boolean isSingleLevelTopic() {
		return StringUtils.endsWithAny(topicIdentifer, SINGLE_LEVEL);
	}

	public boolean isMultiLevelTopic() {
		return StringUtils.endsWithAny(topicIdentifer, MULTI_LEVEL);
	}

	public int getHierarchyDeep(ManagedZone managedZone){
		return StringUtils.countMatches(removeWildcards(getTopicIdentifier(managedZone)), LEVEL_CHAR);
	}
	
	@Override
	public String toString() {
		return topicIdentifer;
	}
}
