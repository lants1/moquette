package io.moquette.plugin;

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
	 * Callback when server has loaded all plugins and is ready to serve requests.
	 */
	public void onServerStarted();

	/**
	 * Called before the broker shutdown. Use this method to safely shutdown the plugin.
	 */
	public void unload();
}
