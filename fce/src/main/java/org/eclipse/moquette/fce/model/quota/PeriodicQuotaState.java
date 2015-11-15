package org.eclipse.moquette.fce.model.quota;

import java.util.Date;

import org.eclipse.moquette.fce.common.SizeUnit;
import org.eclipse.moquette.fce.model.ManagedCycle;

public class PeriodicQuotaState extends QuotaState {

	ManagedCycle cycle;
	Date lastManagedTimestamp;

	public PeriodicQuotaState(ManagedCycle cycle, int maxQuota, int currentQuota, int maxQuotaCount,
			int currentQuotaCount, SizeUnit sizeUnit) {
		super(maxQuota, currentQuota, maxQuotaCount, currentQuotaCount, sizeUnit);
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
}
