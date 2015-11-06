package org.eclipse.moquette.plugin;

/**
 * General Interface for moquette-plugins.
 * 
 * @author lants1
 *
 */
public interface MoquettePlugin {
	
	public void load(MoquetteOperator brokerOperator);
	
	public void unload();
}
