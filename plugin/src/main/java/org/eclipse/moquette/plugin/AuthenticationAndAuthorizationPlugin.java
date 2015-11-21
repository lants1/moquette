package org.eclipse.moquette.plugin;

/**
 * Specific Interface for Broker Authorization Plugins.
 * 
 * @author lants1
 *
 */
public interface AuthenticationAndAuthorizationPlugin extends BrokerPlugin {
	
    boolean canDoOperation(AuthorizationProperties props, MqttAction operation);
    
    boolean checkValid(AuthenticationProperties authProps);
}
