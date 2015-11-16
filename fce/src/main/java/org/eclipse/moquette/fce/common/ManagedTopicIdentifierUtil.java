package org.eclipse.moquette.fce.common;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.moquette.fce.model.ManagedInformation;

public final class ManagedTopicIdentifierUtil {

	public static String moveManagedTopicFilterToOtherZone(String topicFilter, ManagedZone oldZone, ManagedZone newZone){
		return StringUtils.replace(topicFilter, oldZone.getTopicPrefix(), newZone.getTopicPrefix());
	}
	
	public static String addUserSpecificTopicFilter(String topicFilter, ManagedInformation managedInfo){
		try {
			return topicFilter + "/_" + FceHashUtil.getHash(managedInfo);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			new RuntimeException(e);
		}
		// TODO lants1 better implementation.....
		return null;
	}
}
