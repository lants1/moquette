package org.eclipse.moquette.fce.model.quota;

import java.util.Date;

import org.eclipse.moquette.fce.model.ManagedCycle;
import org.eclipse.moquette.plugin.AuthorizationProperties;

public class PeriodicQuota extends Quota {

	ManagedCycle cycle;
	Date lastManagedTimestamp;

	public PeriodicQuota(ManagedCycle cycle, QuotaState state) {
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
	public boolean isValid(AuthorizationProperties props) {
		return 	this.getState().isValid(props);
	}
}
