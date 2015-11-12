package org.eclipse.moquette.plugin;


/**
 * Specific Interface for Broker Interception Plugins.
 * 
 * @author lants1
 *
 */
public interface InterceptionPlugin extends BrokerPlugin {
	
    void onConnect(BrokerInterceptionMessage msg);

    void onDisconnect(BrokerInterceptionMessage msg);

    void onPublish(BrokerInterceptionMessage msg);

    void onSubscribe(BrokerInterceptionMessage msg);

    void onUnsubscribe(BrokerInterceptionMessage msg);
}
