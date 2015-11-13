package org.eclipse.moquette.fce.model.quota;

import org.eclipse.moquette.fce.common.SizeUnit;

public abstract class QuotaState {

	private String usergroup;
	
	private int maxQuota;
	private int currentQuota;
	
	private SizeUnit sizeUnit;
	
	private int maxQuotaCount;
	private int currentQuotaCount;
	
	public QuotaState(int maxQuota, int currentQuota, int maxQuotaCount, int currentQuotaCount, SizeUnit sizeUnit) {
		super();
		this.maxQuota = maxQuota;
		this.currentQuota = currentQuota;
		this.maxQuotaCount = maxQuotaCount;
		this.currentQuotaCount = currentQuotaCount;
		this.sizeUnit = sizeUnit;
	}

	public int getMaxQuotaByte() {
		return maxQuota;
	}

	public void setMaxQuotaByte(int maxQuotaByte) {
		this.maxQuota = maxQuotaByte;
	}

	public int getCurrentQuotaBytes() {
		return currentQuota;
	}

	public void setCurrentQuotaBytes(int currentQuotaBytes) {
		this.currentQuota = currentQuotaBytes;
	}

	public int getMaxQuotaCount() {
		return maxQuotaCount;
	}

	public void setMaxQuotaCount(int maxQuotaCount) {
		this.maxQuotaCount = maxQuotaCount;
	}

	public int getCurrentQuotaCount() {
		return currentQuotaCount;
	}

	public void setCurrentQuotaCount(int currentQuotaCount) {
		this.currentQuotaCount = currentQuotaCount;
	}

	public String getUsergroup() {
		return usergroup;
	}

	public void setUsergroup(String usergroup) {
		this.usergroup = usergroup;
	}

	public SizeUnit getSizeUnit() {
		return sizeUnit;
	}

	public void setSizeUnit(SizeUnit sizeUnit) {
		this.sizeUnit = sizeUnit;
	}

	
}
