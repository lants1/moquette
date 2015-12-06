package io.moquette.fce.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import io.moquette.fce.exception.FceAuthorizationException;
import io.moquette.fce.model.common.ManagedTopic;
import io.moquette.fce.model.common.ManagedZone;
import io.moquette.fce.model.configuration.UserConfiguration;
import io.moquette.plugin.IBrokerOperator;
import io.moquette.plugin.MqttAction;

/**
 * Configuration DB Service singleton which provides storage and utility methods
 * for UserConfigurations a defined ManagedScope.
 * 
 * @author lants1
 *
 */
public class ConfigurationStore extends ManagedZoneInMemoryStore {

	private Map<String, UserConfiguration> configs = new HashMap<>();

	public ConfigurationStore(IBrokerOperator brokerOperator, ManagedZone zone) {
		super(brokerOperator, zone);
	}

	public Set<Entry<String, UserConfiguration>> getAll() {
		return configs.entrySet();
	}

	@Override
	protected UserConfiguration get(ManagedTopic topic, String usernameHash, MqttAction operation) {
		if (configs.get(topic.getIdentifier(usernameHash, getZone())) != null) {
			return configs.get(topic.getIdentifier(usernameHash, getZone()));
		}
		return configs.get(topic.getAllIdentifier(getZone()));
	}

	public void put(String topicIdentifier, UserConfiguration userConfig) {
		configs.put(topicIdentifier, userConfig);
	}

	@Override
	protected Map<String, ?> getStore() {
		return configs;
	}

	public ManagedStorageSearchResult getConfiguration(String topicStr, String usernameHash) throws FceAuthorizationException {
		return getManagedInformation(topicStr, usernameHash, null);
	}

	public List<UserConfiguration> getAllForTopic(ManagedTopic topic) {
		List<UserConfiguration> result = new ArrayList<>();
		for (Entry<String, UserConfiguration> entry : getAll()) {
			ManagedTopic topicStore = new ManagedTopic(entry.getKey());
			if(topicStore.getIdentifier(getZone()).equals(topic.getIdentifier(getZone()))){
				result.add(entry.getValue());
			}
		}
		return result;
	}
}
