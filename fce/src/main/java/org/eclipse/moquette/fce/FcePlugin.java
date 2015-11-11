package org.eclipse.moquette.fce;

import java.util.Properties;

import org.eclipse.moquette.fce.service.MqttDataStore;
import org.eclipse.moquette.plugin.BrokerAuthorizationPlugin;
import org.eclipse.moquette.plugin.BrokerOperator;

public class FcePlugin implements BrokerAuthorizationPlugin {

	MqttDataStore dataStore;
	
	@Override
	public void load(Properties config, BrokerOperator brokerOperator) {
		dataStore = new MqttDataStore(config);
		dataStore.initializeDataStore();
	}

	@Override
	public void unload() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean canWrite(String topic, String user, String client) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canRead(String topic, String user, String client) {
		// TODO Auto-generated method stub
		return false;
	}
}
