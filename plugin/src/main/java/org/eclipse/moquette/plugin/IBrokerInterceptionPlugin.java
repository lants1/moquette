package org.eclipse.moquette.plugin;


/**
 * Specific Interface for Broker Interception Plugins.
 * 
 * @author lants1
 *
 */
public interface IBrokerInterceptionPlugin extends IBrokerPlugin {
	
    void onConnect(BrokerInterceptionMessage msg);

    void onDisconnect(BrokerInterceptionMessage msg);

    void onPublish(BrokerInterceptionMessage msg);

    void onSubscribe(BrokerInterceptionMessage msg);

    void onUnsubscribe(BrokerInterceptionMessage msg);
}
