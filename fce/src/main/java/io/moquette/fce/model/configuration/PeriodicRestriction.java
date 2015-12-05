package io.moquette.fce.model.configuration;

import io.moquette.fce.model.common.DataUnit;
import io.moquette.fce.model.common.ManagedCycle;
import io.moquette.fce.service.FceServiceFactory;
import io.moquette.plugin.AuthorizationProperties;
import io.moquette.plugin.MqttAction;

/**
 * Periodic Restriction
 * 
 * @author lants1
 *
 */
public class PeriodicRestriction extends Restriction{

	private ManagedCycle cyle;

	public PeriodicRestriction(FceAction mqttAction, ManagedCycle cycle, int messageCount, int maxMessageSizeKb, int totalMessageSizeKb, DataUnit sizeUnit, DataSchema dataSchema) {
		super(mqttAction, messageCount, maxMessageSizeKb, totalMessageSizeKb, sizeUnit, dataSchema);
		this.cyle = cycle;
	}

	public ManagedCycle getCyle() {
		return cyle;
	}

	public void setCyle(ManagedCycle cyle) {
		this.cyle = cyle;
	}
	
	@Override
	public boolean isValid(FceServiceFactory services, AuthorizationProperties props, MqttAction operation) {
		return isValidCommon(services, props);
	}
}
