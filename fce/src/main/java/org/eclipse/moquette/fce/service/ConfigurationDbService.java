package org.eclipse.moquette.fce.service;

import java.util.HashMap;

import org.eclipse.moquette.fce.common.ManagedZone;
import org.eclipse.moquette.fce.model.configuration.UserConfiguration;
import org.eclipse.moquette.plugin.BrokerOperator;

public class ConfigurationDbService extends ManagedZoneInMemoryDbService {

	private HashMap<String, UserConfiguration> configStore = new HashMap<>();

	public ConfigurationDbService(BrokerOperator brokerOperator) {
		super(brokerOperator, ManagedZone.MANAGED_CONFIGURATION);
	}

	public UserConfiguration get(String topicIdentifier) {
		return configStore.get(topicIdentifier);
	}

	public void put(String topicIdentifier, UserConfiguration userConfig) {
		configStore.put(topicIdentifier, userConfig);
	}

	@Override
	protected HashMap<String, ?> getStore() {
		return configStore;
	}

	
}
