package org.eclipse.moquette.fce.service;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.moquette.fce.common.ManagedZone;
import org.eclipse.moquette.fce.exception.FceNoAuthorizationPossibleException;
import org.eclipse.moquette.fce.model.ManagedTopic;
import org.eclipse.moquette.fce.model.configuration.UserConfiguration;
import org.eclipse.moquette.fce.model.quota.UserQuotaData;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.BrokerOperator;
import org.eclipse.moquette.plugin.MqttOperation;

public class QuotaDbService extends ManagedZoneInMemoryDbService {

	private HashMap<String, UserQuotaData> quotaStore = new HashMap<>();

	public QuotaDbService(BrokerOperator brokerOperator) {
		super(brokerOperator, ManagedZone.MANAGED_QUOTA);
	}

	public UserQuotaData get(String topicIdentifier) {
		return quotaStore.get(topicIdentifier);
	}

	public Set<Entry<String, UserQuotaData>> getAll() {
		return quotaStore.entrySet();
	}

	public void put(String topicIdentifier, UserQuotaData quota, boolean ignoreTimestamp) throws FceNoAuthorizationPossibleException {
		if (ignoreTimestamp || quota.getTimestamp().after(get(topicIdentifier).getTimestamp())) {
			quotaStore.put(topicIdentifier, quota);
		}
		throw new FceNoAuthorizationPossibleException("outdateddata");
	}

	@Override
	protected HashMap<String, ?> getStore() {
		return quotaStore;
	}

	@Override
	protected UserQuotaData get(ManagedTopic topic, AuthorizationProperties props, MqttOperation operation) {
		if (quotaStore.get(topic.getUserTopicIdentifier(props, getZone(), operation)) != null) {
			return quotaStore.get(topic.getUserTopicIdentifier(props, getZone()));
		}
		return quotaStore.get(topic.getEveryoneTopicIdentifier(getZone(), operation));
	}

	public UserQuotaData getQuota(AuthorizationProperties props, MqttOperation operation)
			throws FceNoAuthorizationPossibleException {
		return (UserQuotaData) getManagedInformation(props, operation);
	}

	public void initializeQuotas(UserConfiguration msgIntent) {
		// TODO lants1
		
	}
}