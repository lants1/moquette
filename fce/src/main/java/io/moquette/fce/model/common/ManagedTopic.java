package io.moquette.fce.model.common;

import org.apache.commons.lang3.StringUtils;

import io.moquette.fce.common.util.ManagedZoneUtil;
import io.moquette.fce.exception.FceSystemException;
import io.moquette.fce.model.ManagedInformation;
import io.moquette.plugin.MqttAction;

public class ManagedTopic {

	public static final String USER_PREFIX = "/_::!";
	public static final String LEVEL_CHAR = "/";
	public static final String ALL_TOPIC = USER_PREFIX + "all";

	private final String topicIdentifer;

	public ManagedTopic(String topicIdentifer) {
		super();
		if (!topicIdentifer.startsWith(LEVEL_CHAR)) {
			throw new FceSystemException("invalid topicfilter which doesn't start with /");
		}
		this.topicIdentifer = topicIdentifer;
	}

	/**
	 * Gets the topicIdentifier of a ManagedTopic.
	 * 
	 * @return String topicIdentifier 
	 */
	public String getIdentifer() {
		return topicIdentifer;
	}

	/**
	 * 
	 * @param managedZone
	 * @return String topicIdentifier
	 */
	public String getIdentifier(ManagedZone managedZone) {
		return ManagedZoneUtil.moveTopicToZone(topicIdentifer, managedZone);
	}

	public String getIdentifier(ManagedInformation managedInfo, ManagedZone zone) {
		if (StringUtils.isEmpty(managedInfo.getUserHash())) {
			return getAllIdentifier(zone);
		}
		return ManagedZoneUtil.moveTopicToZone(topicIdentifer, USER_PREFIX + managedInfo.getUserHash(), zone);
	}

	public String getIdentifier(String userHash, ManagedZone zone) {
		if (StringUtils.isEmpty(userHash)) {
			return getAllIdentifier(zone);
		}
		return ManagedZoneUtil.moveTopicToZone(topicIdentifer, USER_PREFIX + userHash, zone);
	}

	public String getAllIdentifier(ManagedZone zone) {
		return ManagedZoneUtil.moveTopicToZone(topicIdentifer, ALL_TOPIC, zone);
	}

	public String getIdentifier(ManagedInformation managedInfo, ManagedZone zone, MqttAction operation) {
		if (StringUtils.isEmpty(managedInfo.getUserHash())) {
			return getAllIdentifier(zone, operation);
		}

		return ManagedZoneUtil.moveTopicToZone(topicIdentifer, USER_PREFIX + managedInfo.getUserHash(), operation,
				zone);
	}

	public String getIdentifier(String userHash, ManagedZone zone, MqttAction operation) {
		return ManagedZoneUtil.moveTopicToZone(topicIdentifer, USER_PREFIX + userHash, operation, zone);
	}

	public String getAllIdentifier(ManagedZone zone, MqttAction operation) {
		return ManagedZoneUtil.moveTopicToZone(topicIdentifer, ALL_TOPIC, operation, zone);
	}

	public boolean isInManagedArea() {
		return ManagedZoneUtil.isInManagedStore(topicIdentifer);
	}

	public int getHierarchyDeep(ManagedZone managedZone) {
		return StringUtils.countMatches(getIdentifier(managedZone), LEVEL_CHAR);
	}

	public boolean isAllowedForUser(String userHash) {
		return StringUtils.endsWithAny(topicIdentifer, ALL_TOPIC, USER_PREFIX + userHash);
	}

	@Override
	public String toString() {
		return topicIdentifer;
	}
}
