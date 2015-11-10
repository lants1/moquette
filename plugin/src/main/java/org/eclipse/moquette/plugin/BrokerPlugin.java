package org.eclipse.moquette.plugin;

/**
 * General Interface for broker-plugins.
 * 
 * @author lants1
 *
 */
public interface BrokerPlugin {
	
	public void load(BrokerOperator brokerOperator);
	
	public void unload();
	
	public void onEvent();
}
