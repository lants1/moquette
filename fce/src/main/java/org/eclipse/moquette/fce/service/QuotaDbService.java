package org.eclipse.moquette.fce.service;

import java.util.HashMap;

import org.eclipse.moquette.fce.model.quota.Quota;

public class QuotaDbService {

	private HashMap<String, Quota> quotaStore = new HashMap<>(); 
	
	public Quota get(String topicIdentifier) {
		return quotaStore.get(topicIdentifier);
	}

	public void put(String topicIdentifier, Quota quota) {
		quotaStore.put(topicIdentifier, quota);
	}
}
