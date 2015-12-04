package io.moquette.reporting;

import io.moquette.plugin.BrokerInterceptionMessage;
import io.moquette.plugin.IBrokerConfig;
import io.moquette.plugin.IBrokerInterceptionPlugin;
import io.moquette.plugin.IBrokerOperator;

public class ReportingPlugin implements IBrokerInterceptionPlugin {

	private static final String PLUGIN_IDENTIFIER = "Reporting-Plugin";
	
	@Override
	public void load(IBrokerConfig config, IBrokerOperator brokerOperator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unload() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnect(BrokerInterceptionMessage msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnect(BrokerInterceptionMessage msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPublish(BrokerInterceptionMessage msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSubscribe(BrokerInterceptionMessage msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUnsubscribe(BrokerInterceptionMessage msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onServerStarted() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String getPluginIdentifier() {
		return PLUGIN_IDENTIFIER;
	}

}
