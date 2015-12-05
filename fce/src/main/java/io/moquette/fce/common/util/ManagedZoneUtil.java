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

	public static boolean isInManagedStore(String topic) {
		for (ManagedZone zone :  EnumSet.allOf(ManagedZone.class)) {
			if (topic.startsWith(zone.getTopicPrefix())) {
				return true;
			}
		}
		return false;
	}

	public static boolean isInManagedReadableStore(String topic) {
		for (ManagedZone zone : EnumSet.allOf(ManagedZone.class)) {
			if (topic.startsWith(zone.getTopicPrefix()) && zone.getPermission().isReadable()) {
				return true;
			}
		}
		return false;
	}

	public static ManagedZone getZoneForTopic(String topic) {
		for (ManagedZone zone : EnumSet.allOf(ManagedZone.class)) {
			if (topic.startsWith(zone.getTopicPrefix())) {
				return zone;
			}
		}
		return null;
	}

	public static String moveTopicToZone(String topic, ManagedZone topicZone) {
		return moveTopicToZone(topic, "", topicZone);
	}

	public static String moveTopicToZone(String topic, String user, ManagedZone topicZone) {
		String tWithoutZoneAndUser = removeZoneAndUserIdentifier(topic, topicZone);
		return topicZone.getTopicPrefix() + tWithoutZoneAndUser + user;
	}

	public static String moveTopicToZone(String topic, String user, MqttAction action, ManagedZone topicZone) {
		String tWithoutZoneAndUser = removeZoneAndUserIdentifier(topic, topicZone);

		final String publishTopicLevel = ManagedTopic.LEVEL_CHAR + MqttAction.PUBLISH.getValue();
		final String subscribeTopicLevel = ManagedTopic.LEVEL_CHAR + MqttAction.SUBSCRIBE.getValue();

		String pureTopic = tWithoutZoneAndUser;
		if (StringUtils.endsWithAny(tWithoutZoneAndUser, publishTopicLevel, subscribeTopicLevel)) {
			pureTopic = tWithoutZoneAndUser.substring(0, tWithoutZoneAndUser.lastIndexOf(ManagedTopic.LEVEL_CHAR));
		}
		return topicZone.getTopicPrefix() + pureTopic + ManagedTopic.LEVEL_CHAR + action.getValue() + user;
	}

	private static String removeZoneAndUserIdentifier(String topic, ManagedZone topicZone) {
		String tWithoutZone = removeZoneIdentifier(topic);

		if (StringUtils.contains(tWithoutZone, ManagedTopic.USER_PREFIX)) {
			tWithoutZone = tWithoutZone.substring(0, tWithoutZone.lastIndexOf(ManagedTopic.USER_PREFIX));
		}
		return tWithoutZone;
	}

	public static String removeZoneIdentifier(String topic) {
		for (ManagedZone zone : EnumSet.allOf(ManagedZone.class)) {
			if (topic.startsWith(zone.getTopicPrefix())) {
				return StringUtils.removeStart(topic, zone.getTopicPrefix());
			}
		}
		return topic;
	}
}
