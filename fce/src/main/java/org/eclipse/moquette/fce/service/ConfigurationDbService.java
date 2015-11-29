package org.eclipse.moquette.fce.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.moquette.fce.common.ManagedZone;
import org.eclipse.moquette.fce.exception.FceAuthorizationException;
import org.eclipse.moquette.fce.model.ManagedTopic;
import org.eclipse.moquette.fce.model.configuration.UserConfiguration;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.IBrokerOperator;
import org.eclipse.moquette.plugin.MqttAction;

public class ConfigurationDbService extends ManagedZoneInMemoryDbService {

	private HashMap<String, UserConfiguration> configStore = new HashMap<>();

	public ConfigurationDbService(IBrokerOperator brokerOperator, ManagedZone zone) {
		super(brokerOperator, zone);
	}
	
	public Set<Entry<String, UserConfiguration>> getAll() {
		return configStore.entrySet();
	}

	@Override
	protected UserConfiguration get(ManagedTopic topic, AuthorizationProperties props, MqttAction operation) {
		if (configStore.get(topic.getIdentifier(props, getZone())) != null) {
			return configStore.get(topic.getIdentifier(props, getZone()));
		}
		return configStore.get(topic.getAllIdentifier(getZone()));
	}

	public void put(String topicIdentifier, UserConfiguration userConfig) {
		configStore.put(topicIdentifier, userConfig);
	}

	@Override
	protected HashMap<String, ?> getStore() {
		return configStore;
	}

	public UserConfiguration getConfiguration(AuthorizationProperties props) throws FceAuthorizationException{
		return (UserConfiguration) getManagedInformation(props, null);
	}
	
	public List<UserConfiguration> getAllForTopic(ManagedTopic topic) {
		List<UserConfiguration> result = new ArrayList<>();
		for (Entry<String, UserConfiguration> entry : getAll()) {
			if(entry.getKey().startsWith(topic.getIdentifier(getZone()))){
				result.add(entry.getValue());
			}
		}
		return result;
	}
}
