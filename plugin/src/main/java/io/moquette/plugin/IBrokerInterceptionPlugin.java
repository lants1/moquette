package io.moquette.plugin;


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
	 * @param msg BrokerInterceptionMessage
	 */
    void onConnect(BrokerInterceptionMessage msg);

    /**
     * Called on every onDisconnect
     * 
     * @param msg BrokerInterceptionMessage
     */
    void onDisconnect(BrokerInterceptionMessage msg);

    /**
     * Called on every onPublish
     * 
     * @param msg BrokerInterceptionMessage
     */
    void onPublish(BrokerInterceptionMessage msg);

    /**
     * Called on every onSubscribe
     * 
     * @param msg BrokerInterceptionMessage
     */
    void onSubscribe(BrokerInterceptionMessage msg);

    /**
     * Called on every onUnsubscribe
     * 
     * @param msg BrokerInterceptionMessage
     */
    void onUnsubscribe(BrokerInterceptionMessage msg);
}
