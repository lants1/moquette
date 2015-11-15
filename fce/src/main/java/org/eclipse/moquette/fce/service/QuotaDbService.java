package org.eclipse.moquette.fce.service;

import java.util.HashMap;

import org.eclipse.moquette.fce.common.ManagedZone;
import org.eclipse.moquette.fce.model.quota.Quota;
import org.eclipse.moquette.plugin.BrokerOperator;

public class QuotaDbService extends ManagedZoneInMemoryDbService {

	private HashMap<String, Quota> quotaStore = new HashMap<>(); 
	
	public QuotaDbService(BrokerOperator brokerOperator) {
		super(brokerOperator, ManagedZone.MANAGED_QUOTA);
	}

	public Quota get(String topicIdentifier) {
		return quotaStore.get(topicIdentifier);
	}

	public void put(String topicIdentifier, Quota quota) {
		quotaStore.put(topicIdentifier, quota);
	}

	@Override
	protected HashMap<String, ?> getStore() {
		return quotaStore;
	}
	
}
