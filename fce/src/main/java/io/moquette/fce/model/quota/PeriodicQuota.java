package io.moquette.fce.model.quota;

import java.util.Date;

import io.moquette.fce.model.common.ManagedCycle;
import io.moquette.fce.service.FceServiceFactory;
import io.moquette.plugin.AuthorizationProperties;
import io.moquette.plugin.MqttAction;

/**
 * PeriodicQuota for a ManagedCycle e.g. Weekly or Daily...
 * 
 * @author lants1
 *
 */
public class PeriodicQuota extends Quota {

	ManagedCycle cycle;
	Date lastManagedTimestamp;

	public PeriodicQuota(ManagedCycle cycle, IQuotaState state) {
		super(state);
		this.cycle = cycle;
		this.lastManagedTimestamp = new Date();
	}

	public ManagedCycle getCycle() {
		return cycle;
	}

	public void setCycle(ManagedCycle cycle) {
		this.cycle = cycle;
	}

	public Date getLastManagedTimestamp() {
		return lastManagedTimestamp;
	}

	public void setLastManagedTimestamp(Date lastManagedTimestamp) {
		this.lastManagedTimestamp = lastManagedTimestamp;
	}

	@Override
	public boolean isValid(FceServiceFactory services, AuthorizationProperties props, MqttAction operation) {
		return getState().isValid(services, props, operation);
	}
	
}
