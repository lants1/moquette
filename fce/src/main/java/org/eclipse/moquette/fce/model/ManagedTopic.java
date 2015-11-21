package org.eclipse.moquette.fce.model;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.moquette.fce.common.FceHashUtil;
import org.eclipse.moquette.fce.common.ManagedZone;
import org.eclipse.moquette.fce.common.ManagedZoneUtil;
import org.eclipse.moquette.fce.exception.FceSystemFailureException;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.MqttOperation;

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
		return ManagedZoneUtil.moveTopicToZone(topicIdentifer, managedZone);
	}

	public String getUserTopicIdentifier(ManagedInformation managedInfo, ManagedZone zone) {
		if(StringUtils.isEmpty(managedInfo.getUserIdentifier())){
			return getEveryoneTopicIdentifier(zone);
		}
		return ManagedZoneUtil.moveTopicToZone(topicIdentifer + USER_PREFIX + FceHashUtil.getFceHash(managedInfo),
				zone);
	}

	public String getUserTopicIdentifier(AuthorizationProperties authProps, ManagedZone zone) {
		if(StringUtils.isEmpty(authProps.getClientId())){
			return getEveryoneTopicIdentifier(zone);
		}
		return ManagedZoneUtil.moveTopicToZone(topicIdentifer + USER_PREFIX + authProps.getClientId(), zone);
	}

	public String getEveryoneTopicIdentifier(ManagedZone zone) {
		return ManagedZoneUtil.moveTopicToZone(topicIdentifer + EVERYONE_TOPIC, zone);
	}

	public String getUserTopicIdentifier(ManagedInformation managedInfo, ManagedZone zone, MqttOperation operation) {
		if(StringUtils.isEmpty(managedInfo.getUserIdentifier())){
			return getEveryoneTopicIdentifier(zone, operation);
		}
		
		return ManagedZoneUtil.moveTopicToZone(
				topicIdentifer + LEVEL_CHAR + operation.getValue() + USER_PREFIX + FceHashUtil.getFceHash(managedInfo),
				zone);
	}

	public String getUserTopicIdentifier(AuthorizationProperties authProps, ManagedZone zone, MqttOperation operation) {
		return ManagedZoneUtil.moveTopicToZone(
				topicIdentifer + LEVEL_CHAR + operation.getValue() + USER_PREFIX + authProps.getClientId(), zone);
	}

	public String getEveryoneTopicIdentifier(ManagedZone zone, MqttOperation operation) {
		return ManagedZoneUtil.moveTopicToZone(topicIdentifer + LEVEL_CHAR + operation.getValue() + EVERYONE_TOPIC,
				zone);
	}

	public int getHierarchyDeep(ManagedZone managedZone) {
		return StringUtils.countMatches(getTopicIdentifier(managedZone), LEVEL_CHAR);
	}

	@Override
	public String toString() {
		return topicIdentifer;
	}
}
