package org.eclipse.moquette.plugin;

import java.util.Properties;

/**
 * General Interface for broker-plugins.
 * 
 * @author lants1
 *
 */
public interface BrokerPlugin {
	
	public void load(Properties config, BrokerOperator brokerOperator);
	
	public void unload();
}
