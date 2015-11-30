package org.eclipse.moquette.plugin;

/**
 * General Interface for broker-plugins.
 * 
 * @author lants1
 *
 */
public interface IBrokerPlugin {

	public String getPluginIdentifier();

	/**
	 * Called before Broker starts operating (accept requests on initialized
	 * ports). The plugin has the posibility to initialize itself by the use of this method.
	 * 
	 * @param config
	 * @param brokerOperator
	 */
	public void load(IBrokerConfig config, IBrokerOperator brokerOperator);

	/**
	 * Called before the broker shutdown. Use this method to safely shutdown the plugin.
	 */
	public void unload();
}
