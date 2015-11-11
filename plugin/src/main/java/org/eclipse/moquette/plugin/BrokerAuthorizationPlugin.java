package org.eclipse.moquette.plugin;

/**
 * Specific Interface for Broker Authorization Plugins.
 * 
 * @author lants1
 *
 */
public interface BrokerAuthorizationPlugin extends BrokerPlugin {
	
    boolean canWrite(String topic, String user, String client);

    boolean canRead(String topic, String user, String client);
}
