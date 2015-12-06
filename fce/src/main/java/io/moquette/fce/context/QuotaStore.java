package io.moquette.fce.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import java.util.Set;

import io.moquette.fce.exception.FceAuthorizationException;
import io.moquette.fce.model.common.ManagedTopic;
import io.moquette.fce.model.common.ManagedZone;
import io.moquette.fce.model.quota.UserQuota;
import io.moquette.plugin.IBrokerOperator;
import io.moquette.plugin.MqttAction;

/**
 * QuotaDbService singleton which provides storage and utility methods for
 * UserQuota a defined ManagedScope.
 * 
 * @author lants1
 *
 */
public class QuotaStore extends ManagedZoneInMemoryStore {

	private Map<String, UserQuota> quotas = new HashMap<>();

	public QuotaStore(IBrokerOperator brokerOperator, ManagedZone zone) {
		super(brokerOperator, zone);
	}

	public UserQuota get(String topicIdentifier) {
		return quotas.get(topicIdentifier);
	}

	public Set<Entry<String, UserQuota>> getAll() {
		return quotas.entrySet();
	}

	public void put(String topicIdentifier, UserQuota quota) throws FceAuthorizationException {
		put(topicIdentifier, quota, false);
	}

	public void remove(String topicIdentifier) {
		quotas.remove(topicIdentifier);
	}

	public void put(String topicIdentifier, UserQuota quota, boolean ignoreTimestamp) throws FceAuthorizationException {
		UserQuota alreadyStored = get(topicIdentifier);
		if (quota != null) {
			if (ignoreTimestamp || alreadyStored == null
					|| quota.getTimestamp().compareTo(alreadyStored.getTimestamp()) >= 0) {
				quotas.put(topicIdentifier, quota);
				return;
			} else {
				throw new FceAuthorizationException("outdateddata");
			}
		} else {
			// in case of a quota removal...
			quotas.remove(topicIdentifier);
		}
	}

	@Override
	protected Map<String, ?> getStore() {
		return quotas;
	}

	@Override
	protected UserQuota get(ManagedTopic topic, String userHash, MqttAction operation) {
		if (quotas.get(topic.getIdentifier(userHash, getZone(), operation)) != null) {
			return quotas.get(topic.getIdentifier(userHash, getZone(), operation));
		}
		return quotas.get(topic.getAllIdentifier(getZone(), operation));
	}

	public List<UserQuota> getAllForTopic(ManagedTopic topic) {
		List<UserQuota> result = new ArrayList<>();
		for (Entry<String, UserQuota> entry : getAll()) {
			ManagedTopic topicStore = new ManagedTopic(
					StringUtils.remove(StringUtils.remove(entry.getKey(), "/publish"), "/subscribe"));
			if (topicStore.getIdentifier(getZone()).equals(topic.getIdentifier(getZone()))) {
				result.add(entry.getValue());
			}
		}
		return result;
	}

	public ManagedStorageSearchResult getQuota(String topicStr, String usernameHash, MqttAction operation)
			throws FceAuthorizationException {
		return getManagedInformation(topicStr, usernameHash, operation);
	}

}