package org.eclipse.moquette.fce.service;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.moquette.fce.common.ManagedZone;
import org.eclipse.moquette.fce.exception.FceNoAuthorizationPossibleException;
import org.eclipse.moquette.fce.model.ManagedTopic;
import org.eclipse.moquette.fce.model.quota.Quota;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.BrokerOperator;

public class QuotaDbService extends ManagedZoneInMemoryDbService {

	private HashMap<String, Quota> quotaStore = new HashMap<>(); 
	
	public QuotaDbService(BrokerOperator brokerOperator) {
		super(brokerOperator, ManagedZone.MANAGED_QUOTA);
	}

	public Quota get(String topicIdentifier) {
		return quotaStore.get(topicIdentifier);
	}
	
	public Set<Entry<String, Quota>> getAll(){
		return quotaStore.entrySet();
	}

	public void put(String topicIdentifier, Quota quota) {
		quotaStore.put(topicIdentifier, quota);
	}

	@Override
	protected HashMap<String, ?> getStore() {
		return quotaStore;
	}
	
	@Override
	protected Quota get(ManagedTopic topic, AuthorizationProperties props) {
		if (quotaStore.get(topic.getUserTopicIdentifier(props, getZone())) != null) {
			return quotaStore.get(topic.getUserTopicIdentifier(props, getZone()));
		}
		return quotaStore.get(topic.getEveryoneTopicIdentifier(getZone()));
	}
	
	public Quota getQuota(AuthorizationProperties props) throws FceNoAuthorizationPossibleException{
		return (Quota) getManagedInformation(props);
	}
}