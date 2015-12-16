package io.moquette.fce.common.util;

import java.util.EnumSet;
import org.apache.commons.lang3.StringUtils;

import io.moquette.fce.model.common.ManagedTopic;
import io.moquette.fce.model.common.ManagedZone;
import io.moquette.plugin.MqttAction;

/**
 * Utility class for MangedZones.
 * 
 * @author lants1
 *
 */
public final class ManagedZoneUtil {

	private ManagedZoneUtil() {
	}
	
	/**
	 * Is topic identifier in a managed store area?
	 * 
	 * @param topic String
	 * @return true if topic is in managed store area
	 */
	public static boolean isInManagedStore(String topic) {
		for (ManagedZone zone :  EnumSet.allOf(ManagedZone.class)) {
			if (topic.startsWith(zone.getTopicPrefix())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Is topic identifier in a readable managed store area?
	 * 
	 * @param topic String
	 * @return true if topic is in readable managed store area
	 */
	public static boolean isInManagedReadableStore(String topic) {
		for (ManagedZone zone : EnumSet.allOf(ManagedZone.class)) {
			if (topic.startsWith(zone.getTopicPrefix()) && zone.getPermission().isReadable()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the ManagedZone for a topic identifier.
	 * 
	 * @param topic String
	 * @return ManagedZone for topic identifier
	 */
	public static ManagedZone getZoneForTopic(String topic) {
		for (ManagedZone zone : EnumSet.allOf(ManagedZone.class)) {
			if (topic.startsWith(zone.getTopicPrefix())) {
				return zone;
			}
		}
		return null;
	}

	/**
	 * Moves a topic to a managed zone without user identifier.
	 * 
	 * @param topic String
	 * @param topicZone ManagedZone
	 * @return String /%zone%/%topic%
	 */
	public static String moveTopicToZone(String topic, ManagedZone topicZone) {
		return moveTopicToZone(topic, "", topicZone);
	}

	/**
	 * Moves a topic to a managed zone with user identifier.
	 * 
	 * @param topic String
	 * @param user String
	 * @param topicZone ManagedZone
	 * @return String /%zone%/%topic%/%user%
	 */
	public static String moveTopicToZone(String topic, String user, ManagedZone topicZone) {
		String tWithoutZoneAndUser = removeZoneAndUserIdentifier(topic);
		return topicZone.getTopicPrefix() + tWithoutZoneAndUser + user;
	}

	/**
	 * Moves a topic to a managed zone with user identifier and action.
	 * 
	 * @param topic String
	 * @param user String
	 * @param action MqttAction
	 * @param topicZone ManagedZone
	 * @return String /%zone%/%topic%/%action%/%user%
	 */
	public static String moveTopicToZone(String topic, String user, MqttAction action, ManagedZone topicZone) {
		String tWithoutZoneAndUser = removeZoneAndUserIdentifier(topic);

		final String publishTopicLevel = ManagedTopic.LEVEL_CHAR + MqttAction.PUBLISH.getValue();
		final String subscribeTopicLevel = ManagedTopic.LEVEL_CHAR + MqttAction.SUBSCRIBE.getValue();

		String pureTopic = tWithoutZoneAndUser;
		if (StringUtils.endsWithAny(tWithoutZoneAndUser, publishTopicLevel, subscribeTopicLevel)) {
			pureTopic = tWithoutZoneAndUser.substring(0, tWithoutZoneAndUser.lastIndexOf(ManagedTopic.LEVEL_CHAR));
		}
		return topicZone.getTopicPrefix() + pureTopic + ManagedTopic.LEVEL_CHAR + action.getValue() + user;
	}
	
	/**
	 * Returns topic without managed zone identifier.
	 * 
	 * @param topic String
	 * @return String /%topic%
	 */
	public static String removeZoneIdentifier(String topic) {
		for (ManagedZone zone : EnumSet.allOf(ManagedZone.class)) {
			if (topic.startsWith(zone.getTopicPrefix())) {
				return StringUtils.removeStart(topic, zone.getTopicPrefix());
			}
		}
		return topic;
	}

	private static String removeZoneAndUserIdentifier(String topic) {
		String tWithoutZone = removeZoneIdentifier(topic);

		if (StringUtils.contains(tWithoutZone, ManagedTopic.USER_PREFIX)) {
			tWithoutZone = tWithoutZone.substring(0, tWithoutZone.lastIndexOf(ManagedTopic.USER_PREFIX));
		}
		return tWithoutZone;
	}

}
