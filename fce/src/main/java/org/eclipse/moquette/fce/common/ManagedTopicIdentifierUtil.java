package org.eclipse.moquette.fce.common;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.moquette.fce.model.ManagedInformation;

public final class ManagedTopicIdentifierUtil {

	public static String moveManagedTopicFilterToOtherZone(String topicFilter, ManagedZone oldZone, ManagedZone newZone){
		return StringUtils.replace(topicFilter, oldZone.getTopicPrefix(), newZone.getTopicPrefix());
	}
	
	public static String addUserSpecificTopicFilter(String topicFilter, ManagedInformation managedInfo) {
			return topicFilter + "/_" + FceHashUtil.getFceHash(managedInfo);
	}
}
