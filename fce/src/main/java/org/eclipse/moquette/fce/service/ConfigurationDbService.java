package org.eclipse.moquette.fce.service;

import java.util.HashMap;

import org.eclipse.moquette.fce.common.ManagedZone;
import org.eclipse.moquette.fce.common.ManagedZoneUtil;
import org.eclipse.moquette.fce.exception.FceAuthorizationException;
import org.eclipse.moquette.fce.model.ManagedInformation;
import org.eclipse.moquette.fce.model.ManagedTopic;
import org.eclipse.moquette.fce.model.configuration.UserConfiguration;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.BrokerOperator;
import org.eclipse.moquette.plugin.MqttAction;

public class ConfigurationDbService extends ManagedZoneInMemoryDbService {

	private HashMap<String, UserConfiguration> configStore = new HashMap<>();

	public ConfigurationDbService(BrokerOperator brokerOperator, ManagedZone zone) {
		super(brokerOperator, zone);
	}

	@Override
	protected UserConfiguration get(ManagedTopic topic, AuthorizationProperties props, MqttAction operation) {
		if (configStore.get(topic.getIdentifier(props, getZone())) != null) {
			return configStore.get(topic.getIdentifier(props, getZone()));
		}
		return configStore.get(topic.getAllIdentifier(getZone()));
	}

	public void put(String topicIdentifier, UserConfiguration userConfig) {
		configStore.put(ManagedZoneUtil.moveTopicToZone(topicIdentifier, getZone()), userConfig);
	}

	@Override
	protected HashMap<String, ?> getStore() {
		return configStore;
	}

	public UserConfiguration getConfiguration(AuthorizationProperties props) throws FceAuthorizationException{
		ManagedInformation information = getManagedInformation(props, null);
		if(information == null){
			throw new FceAuthorizationException("no userconfiguration found for topic: " + props.getTopic());
		}
		
		return (UserConfiguration) information;
	}
}
