package org.eclipse.moquette.fce.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.moquette.fce.exception.FceAuthorizationException;
import org.eclipse.moquette.fce.model.common.ManagedTopic;
import org.eclipse.moquette.fce.model.common.ManagedZone;
import org.eclipse.moquette.fce.model.configuration.UserConfiguration;
import org.eclipse.moquette.plugin.IBrokerOperator;
import org.eclipse.moquette.plugin.MqttAction;

/**
 * Configuration DB Service singleton which provides storage and utility methods
 * for UserConfigurations a defined ManagedScope.
 * 
 * @author lants1
 *
 */
public class ConfigurationStore extends ManagedZoneInMemoryStore {

	private HashMap<String, UserConfiguration> configStore = new HashMap<>();

	public ConfigurationStore(IBrokerOperator brokerOperator, ManagedZone zone) {
		super(brokerOperator, zone);
	}

	public Set<Entry<String, UserConfiguration>> getAll() {
		return configStore.entrySet();
	}

	@Override
	protected UserConfiguration get(ManagedTopic topic, String usernameHash, MqttAction operation) {
		if (configStore.get(topic.getIdentifier(usernameHash, getZone())) != null) {
			return configStore.get(topic.getIdentifier(usernameHash, getZone()));
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

	public UserConfiguration getConfiguration(String topicStr, String usernameHash) throws FceAuthorizationException {
		return (UserConfiguration) getManagedInformation(topicStr, usernameHash, null);
	}

	public List<UserConfiguration> getAllForTopic(ManagedTopic topic) {
		List<UserConfiguration> result = new ArrayList<>();
		for (Entry<String, UserConfiguration> entry : getAll()) {
			if (entry.getKey().startsWith(topic.getIdentifier(getZone()))) {
				result.add(entry.getValue());
			}
		}
		return result;
	}
}
