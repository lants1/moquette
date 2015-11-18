package org.eclipse.moquette.fce.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.moquette.fce.common.ManagedZone;
import org.eclipse.moquette.fce.common.ManagedZoneUtil;
import org.eclipse.moquette.fce.exception.FceNoAuthorizationPossibleException;
import org.eclipse.moquette.fce.exception.FceSystemFailureException;
import org.eclipse.moquette.fce.model.configuration.UserConfiguration;
import org.eclipse.moquette.plugin.BrokerOperator;

public class ConfigurationDbService extends ManagedZoneInMemoryDbService {

	private static final int COUNT_SLASH_FOR_MANAGED_ZONE = 1;
	private HashMap<String, UserConfiguration> configStore = new HashMap<>();

	public ConfigurationDbService(BrokerOperator brokerOperator) {
		super(brokerOperator, ManagedZone.MANAGED_CONFIGURATION);
	}

	private UserConfiguration get(String topicIdentifier) {
		return configStore.get(topicIdentifier);
	}

	public void put(String topicIdentifier, UserConfiguration userConfig) {
		configStore.put(ManagedZoneUtil.moveTopicIdentifierToZone(topicIdentifier, getZone()), userConfig);
	}

	@Override
	protected HashMap<String, ?> getStore() {
		return configStore;
	}

	public UserConfiguration getConfigurationForSingleManagedTopic(String managedTopicIdentifier) throws FceNoAuthorizationPossibleException{
		if (!managedTopicIdentifier.startsWith("/")) {
			throw new FceSystemFailureException("invalid topicfilter which doesn't start with /");
		}

		String reducedTopicFilter = managedTopicIdentifier;
		while (!reducedTopicFilter.isEmpty()) {
			UserConfiguration userConfig = this.get(reducedTopicFilter);
			if(userConfig != null){
				return userConfig;
			}
			reducedTopicFilter = StringUtils.substringBeforeLast(reducedTopicFilter, "/");
		}
		throw new FceNoAuthorizationPossibleException("no userconfiguration found for topic: "+managedTopicIdentifier);
	}
	
	public List<String> getMangedTopics(String topicFilter) {
		List<String> result = new ArrayList<>();

		if (topicFilter.endsWith("#")) {
			for (Map.Entry<String, UserConfiguration> entry : configStore.entrySet()) {

				String key = entry.getKey();

				if (key.startsWith(
						getZone().getTopicPrefix() + StringUtils.removeEnd(topicFilter, "/#"))) {
					result.add(key);
				}

			}
		} else if (topicFilter.endsWith("+")) {
			int relevantSlashCount = StringUtils.countMatches(topicFilter, "/") + COUNT_SLASH_FOR_MANAGED_ZONE;

			for (Map.Entry<String, UserConfiguration> entry : configStore.entrySet()) {

				String key = entry.getKey();

				if (key.startsWith(
						getZone().getTopicPrefix() + StringUtils.removeEnd(topicFilter, "/+"))) {
					if (StringUtils.countMatches(key, "/") == relevantSlashCount) {
						result.add(key);
					}
				}

			}
		} else {
			result.add(topicFilter);
		}
		return result;
	}

	public boolean isTopicFilterManaged(String topicFilter) {
		List<String> tokens = getTokens(topicFilter);

		for (Map.Entry<String, UserConfiguration> entry : configStore.entrySet()) {

			String key = entry.getKey();
			for (String token : tokens) {
				if (key.startsWith(token)) {
					return true;
				}
			}
		}
		return false;

	}

	private List<String> getTokens(String topicFilter) {
		if (!topicFilter.startsWith("/")) {
			throw new FceSystemFailureException("invalid topicfilter which doesn't start with /");
		}

		List<String> tokens = new ArrayList<>();
		String reducedTopicFilter = topicFilter;
		while (!reducedTopicFilter.isEmpty()) {
			tokens.add(ManagedZoneUtil.moveTopicIdentifierToZone(reducedTopicFilter, getZone()));
			reducedTopicFilter = StringUtils.substringBeforeLast(reducedTopicFilter, "/");
		}
		return tokens;
	}

}
