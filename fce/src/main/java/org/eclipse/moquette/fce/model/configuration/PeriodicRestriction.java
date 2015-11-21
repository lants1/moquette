package org.eclipse.moquette.fce.model.configuration;

import org.eclipse.moquette.fce.common.SizeUnit;
import org.eclipse.moquette.fce.model.ManagedCycle;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.MqttOperation;

/**
 * Periodic Restriction
 * 
 * @author lants1
 *
 */
public class PeriodicRestriction extends Restriction{

	private ManagedCycle cyle;

	public PeriodicRestriction(ManagedCycle cycle, int messageCount, int maxMessageSizeKb, int totalMessageSizeKb, SizeUnit sizeUnit, String wsdlUrl) {
		super(messageCount, maxMessageSizeKb, totalMessageSizeKb, sizeUnit, wsdlUrl);
		this.cyle = cycle;
	}

	public ManagedCycle getCyle() {
		return cyle;
	}

	public void setCyle(ManagedCycle cyle) {
		this.cyle = cyle;
	}
	
	@Override
	public boolean isValid(AuthorizationProperties props, MqttOperation operation) {
		return isValidCommon(props, operation);
	}
}
