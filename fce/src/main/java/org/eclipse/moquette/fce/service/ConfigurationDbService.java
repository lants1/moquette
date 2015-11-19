package org.eclipse.moquette.fce.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.moquette.fce.common.ManagedZone;
import org.eclipse.moquette.fce.common.ManagedZoneUtil;
import org.eclipse.moquette.fce.exception.FceNoAuthorizationPossibleException;
import org.eclipse.moquette.fce.model.ManagedTopic;
import org.eclipse.moquette.fce.model.configuration.UserConfiguration;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.BrokerOperator;

public class ConfigurationDbService extends ManagedZoneInMemoryDbService {

	private HashMap<String, UserConfiguration> configStore = new HashMap<>();

	public ConfigurationDbService(BrokerOperator brokerOperator) {
		super(brokerOperator, ManagedZone.MANAGED_CONFIGURATION);
	}

	private UserConfiguration get(ManagedTopic topic, AuthorizationProperties props) {
		if (configStore.get(topic.getUserTopicIdentifier(props, getZone())) != null) {
			return configStore.get(topic.getUserTopicIdentifier(props, getZone()));
		}
		return configStore.get(topic.getEveryoneTopicIdentifier(getZone()));
	}

	public void put(String topicIdentifier, UserConfiguration userConfig) {
		configStore.put(ManagedZoneUtil.moveTopicIdentifierToZone(topicIdentifier, getZone()), userConfig);
	}

	@Override
	protected HashMap<String, ?> getStore() {
		return configStore;
	}

	public UserConfiguration getConfigurationForSingleManagedTopic(ManagedTopic topic, AuthorizationProperties props)
			throws FceNoAuthorizationPossibleException {
		String reducedTopicFilter = topic.getTopicIdentifer();
		while (!reducedTopicFilter.isEmpty()) {
			UserConfiguration userConfig = get(new ManagedTopic(reducedTopicFilter), props);
			if (userConfig != null) {
				return userConfig;
			}
			reducedTopicFilter = StringUtils.substringBeforeLast(reducedTopicFilter, "/");
		}
		throw new FceNoAuthorizationPossibleException("no userconfiguration found for topic: " + topic);
	}

	public List<String> getMangedTopics(ManagedTopic topic) {
		List<String> result = new ArrayList<>();

		if (topic.isMultiLevelTopic()) {
			for (Map.Entry<String, UserConfiguration> entry : configStore.entrySet()) {

				String key = entry.getKey();

				if (key.startsWith(topic.getTopicIdentifierWithoutWildcards(getZone()))) {
					result.add(key);
				}

			}
		} else if (topic.isSingleLevelTopic()) {
			for (Map.Entry<String, UserConfiguration> entry : configStore.entrySet()) {

				String key = entry.getKey();

				if (key.startsWith(topic.getTopicIdentifierWithoutWildcards(getZone()))) {
					if (new ManagedTopic(key).getHierarchyDeep(getZone()) == topic.getHierarchyDeep(getZone())) {
						result.add(key);
					}
				}

			}
		} else if (topic.isSingleTopic()) {
			result.add(topic.getTopicIdentifer());
		}
		return result;
	}

	public boolean isTopicFilterManaged(ManagedTopic topic) {
		List<String> tokens = getTokens(topic);

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

	private List<String> getTokens(ManagedTopic topic) {
		List<String> tokens = new ArrayList<>();
		String reducedTopicFilter = topic.getTopicIdentifer();
		while (!reducedTopicFilter.isEmpty()) {
			tokens.add(ManagedZoneUtil.moveTopicIdentifierToZone(reducedTopicFilter, getZone()));
			reducedTopicFilter = StringUtils.substringBeforeLast(reducedTopicFilter, "/");
		}
		return tokens;
	}

}
