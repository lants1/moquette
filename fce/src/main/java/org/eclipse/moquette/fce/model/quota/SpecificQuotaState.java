package org.eclipse.moquette.fce.model.quota;

import java.util.Date;

import org.eclipse.moquette.fce.common.SizeUnit;

public class SpecificQuotaState extends QuotaState{

	private Date from;
	private Date to;
	
	public SpecificQuotaState(Date from, Date to, int maxQuota, int currentQuota, int maxQuotaCount, int currentQuotaCount, SizeUnit sizeUnit) {
		super(maxQuota, currentQuota, maxQuotaCount, currentQuotaCount, sizeUnit);
		this.from = from;
		this.to = to;
	}
	public Date getFrom() {
		return from;
	}
	public void setFrom(Date from) {
		this.from = from;
	}
	public Date getTo() {
		return to;
	}
	public void setTo(Date to) {
		this.to = to;
	}

}
