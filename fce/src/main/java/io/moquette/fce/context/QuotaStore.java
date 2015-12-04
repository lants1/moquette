package io.moquette.fce.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
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

	private HashMap<String, UserQuota> quotaStore = new HashMap<>();

	public QuotaStore(IBrokerOperator brokerOperator, ManagedZone zone) {
		super(brokerOperator, zone);
	}

	public UserQuota get(String topicIdentifier) {
		return quotaStore.get(topicIdentifier);
	}

	public Set<Entry<String, UserQuota>> getAll() {
		return quotaStore.entrySet();
	}

	public void put(String topicIdentifier, UserQuota quota) throws FceAuthorizationException {
		put(topicIdentifier, quota, false);
	}

	public void put(String topicIdentifier, UserQuota quota, boolean ignoreTimestamp) throws FceAuthorizationException {
		UserQuota alreadyStored = get(topicIdentifier);
		if (quota != null) {
			if (ignoreTimestamp || alreadyStored == null
					|| quota.getTimestamp().compareTo(alreadyStored.getTimestamp()) >= 0) {
				quotaStore.put(topicIdentifier, quota);
				return;
			}
		}
		throw new FceAuthorizationException("outdateddata");
	}

	@Override
	protected HashMap<String, ?> getStore() {
		return quotaStore;
	}

	@Override
	protected UserQuota get(ManagedTopic topic, String userHash, MqttAction operation) {
		if (quotaStore.get(topic.getIdentifier(userHash, getZone(), operation)) != null) {
			return quotaStore.get(topic.getIdentifier(userHash, getZone(), operation));
		}
		return quotaStore.get(topic.getAllIdentifier(getZone(), operation));
	}

	public List<UserQuota> getAllForTopic(ManagedTopic topic) {
		List<UserQuota> result = new ArrayList<>();
		for (Entry<String, UserQuota> entry : getAll()) {
			if (entry.getKey().startsWith(topic.getIdentifier(getZone()))) {
				result.add(entry.getValue());
			}
		}
		return result;
	}

	public UserQuota getQuota(String topicStr, String usernameHash, MqttAction operation)
			throws FceAuthorizationException {
		return (UserQuota) getManagedInformation(topicStr, usernameHash, operation);
	}

}