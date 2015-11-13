package org.eclipse.moquette.fce.model.quota;

import java.util.List;

public class Quota {

	private String usergroup;
	private List<QuotaState> quotaState;
	
	public Quota(String usergroup, List<QuotaState> quotaState) {
		super();
		this.usergroup = usergroup;
		this.quotaState = quotaState;
	}
	public String getUsergroup() {
		return usergroup;
	}
	public void setUsergroup(String usergroup) {
		this.usergroup = usergroup;
	}
	public List<QuotaState> getQuotaState() {
		return quotaState;
	}
	public void setQuotaState(List<QuotaState> quotaState) {
		this.quotaState = quotaState;
	}
	
	
}
