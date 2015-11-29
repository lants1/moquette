package org.eclipse.moquette.fce.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.moquette.fce.common.ManagedZone;
import org.eclipse.moquette.fce.exception.FceAuthorizationException;
import org.eclipse.moquette.fce.model.ManagedTopic;
import org.eclipse.moquette.fce.model.quota.UserQuota;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.IBrokerOperator;
import org.eclipse.moquette.plugin.MqttAction;

public class QuotaDbService extends ManagedZoneInMemoryDbService {

	private HashMap<String, UserQuota> quotaStore = new HashMap<>();

	public QuotaDbService(IBrokerOperator brokerOperator, ManagedZone zone) {
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

	public void put(String topicIdentifier, UserQuota quota, boolean ignoreTimestamp)
			throws FceAuthorizationException {
		if (ignoreTimestamp || quota.getTimestamp().compareTo(get(topicIdentifier).getTimestamp()) >= 0) {
			quotaStore.put(topicIdentifier, quota);

		} else {
			throw new FceAuthorizationException("outdateddata");
		}
	}

	@Override
	protected HashMap<String, ?> getStore() {
		return quotaStore;
	}

	@Override
	protected UserQuota get(ManagedTopic topic, AuthorizationProperties props, MqttAction operation) {
		if (quotaStore.get(topic.getIdentifier(props, getZone(), operation)) != null) {
			return quotaStore.get(topic.getIdentifier(props, getZone(), operation));
		}
		return quotaStore.get(topic.getAllIdentifier(getZone(), operation));
	}
	
	public List<UserQuota> getAllForTopic(ManagedTopic topic) {
		List<UserQuota> result = new ArrayList<>();
		for (Entry<String, UserQuota> entry : getAll()) {
			if(entry.getKey().startsWith(topic.getIdentifier(getZone()))){
				result.add(entry.getValue());
			}
		}
		return result;
	}

	public UserQuota getQuota(AuthorizationProperties props, MqttAction operation)
			throws FceAuthorizationException {
		return (UserQuota) getManagedInformation(props, operation);
	}

}