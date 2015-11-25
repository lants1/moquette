package org.eclipse.moquette.plugin;

/**
 * General Interface for broker-plugins.
 * 
 * @author lants1
 *
 */
public interface IBrokerPlugin {
	
	public String getPluginIdentifier();
	
	public void load(IBrokerConfig config, IBrokerOperator brokerOperator);
	
	public void unload();
}
