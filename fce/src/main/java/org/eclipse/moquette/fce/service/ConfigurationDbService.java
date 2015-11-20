package org.eclipse.moquette.fce.service;

import java.util.HashMap;

import org.eclipse.moquette.fce.common.ManagedZone;
import org.eclipse.moquette.fce.common.ManagedZoneUtil;
import org.eclipse.moquette.fce.exception.FceNoAuthorizationPossibleException;
import org.eclipse.moquette.fce.model.ManagedInformation;
import org.eclipse.moquette.fce.model.ManagedTopic;
import org.eclipse.moquette.fce.model.configuration.UserConfiguration;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.BrokerOperator;

public class ConfigurationDbService extends ManagedZoneInMemoryDbService {

	private HashMap<String, UserConfiguration> configStore = new HashMap<>();

	public ConfigurationDbService(BrokerOperator brokerOperator) {
		super(brokerOperator, ManagedZone.MANAGED_CONFIGURATION);
	}

	@Override
	protected UserConfiguration get(ManagedTopic topic, AuthorizationProperties props) {
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

	public UserConfiguration getConfiguration(AuthorizationProperties props) throws FceNoAuthorizationPossibleException{
		ManagedInformation information = getManagedInformation(props);
		if(information == null){
			throw new FceNoAuthorizationPossibleException("no userconfiguration found for topic: " + props.getTopic());
		}
		
		return (UserConfiguration) information;
	}
}
