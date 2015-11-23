package org.eclipse.moquette.fce.model.configuration;

import org.eclipse.moquette.fce.common.DataUnit;
import org.eclipse.moquette.fce.model.ManagedCycle;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.MqttAction;

/**
 * Periodic Restriction
 * 
 * @author lants1
 *
 */
public class PeriodicRestriction extends Restriction{

	private ManagedCycle cyle;

	public PeriodicRestriction(FceAction mqttAction, ManagedCycle cycle, int messageCount, int maxMessageSizeKb, int totalMessageSizeKb, DataUnit sizeUnit, String wsdlUrl) {
		super(mqttAction, messageCount, maxMessageSizeKb, totalMessageSizeKb, sizeUnit, wsdlUrl);
		this.cyle = cycle;
	}

	public ManagedCycle getCyle() {
		return cyle;
	}

	public void setCyle(ManagedCycle cyle) {
		this.cyle = cyle;
	}
	
	@Override
	public boolean isValid(AuthorizationProperties props, MqttAction operation) {
		return isValidCommon(props, operation);
	}
}
