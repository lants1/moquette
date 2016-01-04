package io.moquette.plugin;

/**
 * Specific Interface for Broker Authorization Plugins.
 * 
 * @author lants1
 *
 */
public interface IAuthenticationAndAuthorizationPlugin extends IBrokerPlugin {
	
	/**
	 * Broker requests approval of action. Called on every publish or subscribe action.
	 * 
	 * @param props AuthorizationProperties
	 * @param action MqttAction
	 * @return boolean result of validity check
	 */
    boolean canDoOperation(AuthorizationProperties props, MqttAction action);
    
    /**
     * Broker requests authentication approval of user. Called on every connect to the broker. 
     * 
     * @param authProps AuthenticationProperties
     * @return boolean result of validity check
     */
    boolean checkValid(AuthenticationProperties authProps);
}
