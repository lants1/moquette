package io.moquette.fce.model.common;

import org.apache.commons.lang3.StringUtils;

import io.moquette.fce.common.util.ManagedZoneUtil;
import io.moquette.fce.exception.FceSystemException;
import io.moquette.fce.model.ManagedInformation;
import io.moquette.plugin.MqttAction;

/**
 * Representation of a managed topic.
 * 
 * @author lants1
 *
 */
public class ManagedTopic {

	public static final String USER_PREFIX = "/_::!";
	public static final String LEVEL_CHAR = "/";
	public static final String ALL_TOPIC = USER_PREFIX + "all";

	private final String topicIdentifer;

	/**
	 * Constructor for a managedTopic.
	 * 
	 * @param topicIdentifer String
	 */
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
	 * Gets the identifier of a topic with a managed zone prefix.
	 * 
	 * @param managedZone ManagedZone
	 * @return /%managedZone/topicIdentifier String
	 */
	public String getIdentifier(ManagedZone managedZone) {
		return ManagedZoneUtil.moveTopicToZone(topicIdentifer, managedZone);
	}

	/**
	 * Gets the identifier of a topic with a managed zone prefix and an user postfix.
	 * 
	 * @param managedInfo ManagedInformation
	 * @param zone ManagedZone
	 * @return /%zone/%topicIdentifier/%managedInfo.userHash String
	 */
	public String getIdentifier(ManagedInformation managedInfo, ManagedZone zone) {
		if (StringUtils.isEmpty(managedInfo.getUserHash())) {
			return getAllIdentifier(zone);
		}
		return ManagedZoneUtil.moveTopicToZone(topicIdentifer, USER_PREFIX + managedInfo.getUserHash(), zone);
	}

	/**
	 * Gets the identifier of a topic with a managed zone prefix and an user postfix.
	 * 
	 * @param userHash String
	 * @param zone ManagedZone
	 * @return /%zone/%topicIdentifier/%userHash String
	 */
	public String getIdentifier(String userHash, ManagedZone zone) {
		if (StringUtils.isEmpty(userHash)) {
			return getAllIdentifier(zone);
		}
		return ManagedZoneUtil.moveTopicToZone(topicIdentifer, USER_PREFIX + userHash, zone);
	}

	/**
	 * Gets the identifier of a topic with a managed zone prefix.
	 * 
	 * @param zone ManagedZone
	 * @return /%zone/%topicIdentifier String
	 */
	public String getAllIdentifier(ManagedZone zone) {
		return ManagedZoneUtil.moveTopicToZone(topicIdentifer, ALL_TOPIC, zone);
	}

	/**
	 * Gets the identifier of a topic with a managed zone prefix user and action postfix.
	 * 
	 * @param managedInfo ManagedInformation
	 * @param zone ManagedZone
	 * @param operation MqttAction
	 * @return /%zone/%topicIdentifier/%user/%operation String
	 */
	public String getIdentifier(ManagedInformation managedInfo, ManagedZone zone, MqttAction operation) {
		if (StringUtils.isEmpty(managedInfo.getUserHash())) {
			return getAllIdentifier(zone, operation);
		}

		return ManagedZoneUtil.moveTopicToZone(topicIdentifer, USER_PREFIX + managedInfo.getUserHash(), operation,
				zone);
	}

	/**
	 * Gets the identifier of a topic with a managed zone prefix user and action postfix.
	 * 
	 * @param userHash String
	 * @param zone ManagedZone
	 * @param operation MqttAction
	 * @return /%zone/%topicIdentifier/%userHash/%operation String
	 */
	public String getIdentifier(String userHash, ManagedZone zone, MqttAction operation) {
		return ManagedZoneUtil.moveTopicToZone(topicIdentifer, USER_PREFIX + userHash, operation, zone);
	}

	/**
	 * Gets the allIdentifier of a topic with a managed zone prefix and an operation postfix.
	 * 
	 * @param zone ManagedZone
	 * @param operation MqttAction
	 * @return /%zone/%topicIdentifier/%allIdentifier/%operation String
	 */
	public String getAllIdentifier(ManagedZone zone, MqttAction operation) {
		return ManagedZoneUtil.moveTopicToZone(topicIdentifer, ALL_TOPIC, operation, zone);
	}

	/**
	 * Return boolean if topicidentifier is in a managed area. 
	 * 
	 * @return boolean true if a topic identifier is in a managed area.
	 */
	public boolean isInManagedArea() {
		return ManagedZoneUtil.isInManagedStore(topicIdentifer);
	}

	/**
	 * Gets the hierarchy deep of a topic identifier.
	 * 
	 * @param managedZone ManagedZone
	 * @return int hierarchy deepness of a topicidentifier
	 */
	public int getHierarchyDeepness(ManagedZone managedZone) {
		return StringUtils.countMatches(getIdentifier(managedZone), LEVEL_CHAR);
	}

	/**
	 * Is userHash allowed for topicIdentifier.
	 * 
	 * @param userHash String
	 * @return boolean true if userHash is contained in topic identifier or its a everyone identifier
	 */
	public boolean isAllowedForUser(String userHash) {
		return StringUtils.endsWithAny(topicIdentifer, ALL_TOPIC, USER_PREFIX + userHash);
	}

	@Override
	public String toString() {
		return topicIdentifer;
	}
}
