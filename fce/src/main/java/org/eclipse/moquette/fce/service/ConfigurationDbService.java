package org.eclipse.moquette.fce.service;

import java.util.HashMap;

import org.eclipse.moquette.fce.model.configuration.UserConfiguration;

public class ConfigurationDbService {

	private HashMap<String, UserConfiguration> configStore = new HashMap<>();

	public UserConfiguration get(String topicIdentifier) {
		return configStore.get(topicIdentifier);
	}

	public void put(String topicIdentifier, UserConfiguration userConfig) {
		configStore.put(topicIdentifier, userConfig);
	}
}
