package org.eclipse.moquette.plugin;

/**
 * Specific Interface for Broker Authorization Plugins.
 * 
 * @author lants1
 *
 */
public interface AuthenticationAndAuthorizationPlugin extends BrokerPlugin {
	
    boolean canWrite(AuthorizationProperties props);

    boolean canRead(AuthorizationProperties props);
    
    boolean checkValid(AuthenticationProperties authProps);
}
