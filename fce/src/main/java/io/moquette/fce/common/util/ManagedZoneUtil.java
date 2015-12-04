package io.moquette.fce.common.util;

import java.util.EnumSet;
import java.util.Set;

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
		Set<ManagedZone> zones = EnumSet.allOf(ManagedZone.class);
		for (ManagedZone zone : zones) {
			if (topic.startsWith(zone.getTopicPrefix())) {
				return true;
			}
		}
		return false;
	}

	public static boolean isInManagedReadableStore(String topic) {
		Set<ManagedZone> zones = EnumSet.allOf(ManagedZone.class);
		for (ManagedZone zone : zones) {
			if (topic.startsWith(zone.getTopicPrefix()) && zone.getPermission().isReadable()) {
				return true;
			}
		}
		return false;
	}

	public static ManagedZone getZoneForTopic(String topic) {
		Set<ManagedZone> zones = EnumSet.allOf(ManagedZone.class);
		for (ManagedZone zone : zones) {
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

		topic = removeZoneAndUserIdentifier(topic, topicZone);
		return topicZone.getTopicPrefix() + topic + user;
	}

	public static String moveTopicToZone(String topic, String user, MqttAction action, ManagedZone topicZone) {

		topic = removeZoneAndUserIdentifier(topic, topicZone);

		if (StringUtils.endsWith(topic, ManagedTopic.LEVEL_CHAR + MqttAction.PUBLISH.getValue())
				|| StringUtils.endsWith(topic, ManagedTopic.LEVEL_CHAR + MqttAction.SUBSCRIBE.getValue())) {
			topic = topic.substring(0, topic.lastIndexOf(ManagedTopic.LEVEL_CHAR));
		}
		return topicZone.getTopicPrefix() + topic + ManagedTopic.LEVEL_CHAR + action.getValue() + user;
	}

	private static String removeZoneAndUserIdentifier(String topic, ManagedZone topicZone) {
		topic = removeZoneIdentifier(topic);

		if (StringUtils.contains(topic, ManagedTopic.USER_PREFIX)) {
			topic = topic.substring(0, topic.lastIndexOf(ManagedTopic.USER_PREFIX));
		}
		return topic;
	}

	public static String removeZoneIdentifier(String topic) {
		Set<ManagedZone> zones = EnumSet.allOf(ManagedZone.class);
		for (ManagedZone zone : zones) {
			if (topic.startsWith(zone.getTopicPrefix())) {
				return StringUtils.removeStart(topic, zone.getTopicPrefix());
			}
		}
		return topic;
	}
}
