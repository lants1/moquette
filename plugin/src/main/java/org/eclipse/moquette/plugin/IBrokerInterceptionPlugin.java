package org.eclipse.moquette.plugin;


/**
 * Specific Interface for Broker Interception Plugins.
 * 
 * @author lants1
 *
 */
public interface IBrokerInterceptionPlugin extends IBrokerPlugin {
	
	/**
	 * Called on every onConnect
	 * 
	 * @param BrokerInterceptionMessage msg
	 */
    void onConnect(BrokerInterceptionMessage msg);

    /**
     * Called on every onDisconnect
     * 
     * @param BrokerInterceptionMessage msg
     */
    void onDisconnect(BrokerInterceptionMessage msg);

    /**
     * Called on every onPublish
     * 
     * @param BrokerInterceptionMessage msg
     */
    void onPublish(BrokerInterceptionMessage msg);

    /**
     * Called on every onSubscribe
     * 
     * @param BrokerInterceptionMessage msg
     */
    void onSubscribe(BrokerInterceptionMessage msg);

    /**
     * Called on every onUnsubscribe
     * 
     * @param BrokerInterceptionMessage msg
     */
    void onUnsubscribe(BrokerInterceptionMessage msg);
}
