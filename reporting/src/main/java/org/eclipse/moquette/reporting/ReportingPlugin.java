package org.eclipse.moquette.reporting;

import org.eclipse.moquette.plugin.BrokerInterceptionMessage;
import org.eclipse.moquette.plugin.IBrokerConfig;
import org.eclipse.moquette.plugin.IBrokerInterceptionPlugin;
import org.eclipse.moquette.plugin.IBrokerOperator;

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
