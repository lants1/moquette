package org.eclipse.moquette.reporting;

import java.util.Properties;

import org.eclipse.moquette.plugin.BrokerInterceptionMessage;
import org.eclipse.moquette.plugin.InterceptionPlugin;
import org.eclipse.moquette.plugin.BrokerOperator;

public class ReportingPlugin implements InterceptionPlugin {

	private static final String PLUGIN_IDENTIFIER = "Reporting-Plugin";
	
	@Override
	public void load(Properties config, BrokerOperator brokerOperator) {
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
	public String getPluginIdentifier() {
		return PLUGIN_IDENTIFIER;
	}

}
