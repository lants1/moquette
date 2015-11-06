package org.eclipse.moquette.plugin;

/**
 * Specific Interface for Moquette Authorization Plugins.
 * 
 * @author lants1
 *
 */
public interface MoquetteAuthorizationPlugin extends MoquettePlugin {
	
	public boolean isAuthorized();
}
