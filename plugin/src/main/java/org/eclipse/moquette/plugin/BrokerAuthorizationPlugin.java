package org.eclipse.moquette.plugin;

/**
 * Specific Interface for Broker Authorization Plugins.
 * 
 * @author lants1
 *
 */
public interface BrokerAuthorizationPlugin extends BrokerPlugin {
	
	public boolean isAuthorized();
}
