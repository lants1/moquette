package org.eclipse.moquette.fce.model.quota;

import org.eclipse.moquette.fce.common.SizeUnit;
import org.eclipse.moquette.fce.model.common.ManagedCycle;

public class PeriodicQuotaState extends QuotaState {

	ManagedCycle cycle;

	public PeriodicQuotaState(ManagedCycle cycle, int maxQuota, int currentQuota, int maxQuotaCount,
			int currentQuotaCount, SizeUnit sizeUnit) {
		super(maxQuota, currentQuota, maxQuotaCount, currentQuotaCount, sizeUnit);
		this.cycle = cycle;
	}

	public ManagedCycle getCycle() {
		return cycle;
	}

	public void setCycle(ManagedCycle cycle) {
		this.cycle = cycle;
	}

}
