package org.eclipse.moquette.fce.model;

import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.moquette.fce.common.FceHashUtil;
import org.eclipse.moquette.fce.common.ManagedZone;
import org.eclipse.moquette.fce.common.ManagedZoneUtil;
import org.eclipse.moquette.fce.exception.FceSystemException;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.MqttAction;

public class ManagedTopic {

	private final static Logger log = Logger.getLogger(ManagedTopic.class.getName());
	
	public static final String USER_PREFIX = "/_";
	public static final String LEVEL_CHAR = "/";
	public static final String ALL_TOPIC = "/_all";

	private final String topicIdentifer;

	public ManagedTopic(String topicIdentifer) {
		super();
		if (!topicIdentifer.startsWith(LEVEL_CHAR)) {
			throw new FceSystemException("invalid topicfilter which doesn't start with /");
		}
		this.topicIdentifer = topicIdentifer;
	}

	public String getIdentifer() {
		return topicIdentifer;
	}

	public String getIdentifier(ManagedZone managedZone) {
		return ManagedZoneUtil.moveTopicToZone(topicIdentifer, managedZone);
	}

	public String getIdentifier(ManagedInformation managedInfo, ManagedZone zone) {
		if (StringUtils.isEmpty(managedInfo.getUserIdentifier())) {
			return getAllIdentifier(zone);
		}
		return ManagedZoneUtil.moveTopicToZone(topicIdentifer + USER_PREFIX + FceHashUtil.getFceHashWithPepper(managedInfo),
				zone);
	}

	public String getIdentifier(AuthorizationProperties authProps, ManagedZone zone) {
		if (StringUtils.isEmpty(authProps.getClientId())) {
			return getAllIdentifier(zone);
		}
		return ManagedZoneUtil.moveTopicToZone(topicIdentifer + USER_PREFIX + authProps.getClientId(), zone);
	}

	public String getAllIdentifier(ManagedZone zone) {
		return ManagedZoneUtil.moveTopicToZone(topicIdentifer + ALL_TOPIC, zone);
	}

	public String getIdentifier(ManagedInformation managedInfo, ManagedZone zone, MqttAction operation) {
		if (StringUtils.isEmpty(managedInfo.getUserIdentifier())) {
			return getAllIdentifier(zone, operation);
		}

		return ManagedZoneUtil.moveTopicToZone(
				topicIdentifer + LEVEL_CHAR + operation.getValue() + USER_PREFIX + FceHashUtil.getFceHashWithPepper(managedInfo),
				zone);
	}

	public String getIdentifier(AuthorizationProperties authProps, ManagedZone zone, MqttAction operation) {
		return ManagedZoneUtil.moveTopicToZone(
				topicIdentifer + LEVEL_CHAR + operation.getValue() + USER_PREFIX + authProps.getClientId(), zone);
	}
	
	public String getIdentifier(String clientId, ManagedZone zone, MqttAction operation) {
		return ManagedZoneUtil.moveTopicToZone(
				topicIdentifer + LEVEL_CHAR + operation.getValue() + USER_PREFIX + clientId, zone);
	}

	public String getAllIdentifier(ManagedZone zone, MqttAction operation) {
		return ManagedZoneUtil.moveTopicToZone(topicIdentifer + LEVEL_CHAR + operation.getValue() + ALL_TOPIC, zone);
	}

	public boolean isInManagedArea(){
		return ManagedZoneUtil.isInManagedStore(topicIdentifer);
	}
	
	public int getHierarchyDeep(ManagedZone managedZone) {
		return StringUtils.countMatches(getIdentifier(managedZone), LEVEL_CHAR);
	}

	public boolean isAllowedForUser(AuthorizationProperties props){
		boolean result = StringUtils.endsWithAny(topicIdentifer, ALL_TOPIC, USER_PREFIX+props.getClientId());
		log.fine(props.getTopic()+" isAllowedForUser="+result);
		return result;
	}
	
	@Override
	public String toString() {
		return topicIdentifer;
	}
}
