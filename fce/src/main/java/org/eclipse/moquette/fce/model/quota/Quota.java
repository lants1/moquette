package org.eclipse.moquette.fce.model.quota;

import java.util.List;

import org.eclipse.moquette.fce.model.ManagedInformation;
import org.eclipse.moquette.plugin.AuthorizationProperties;

public class Quota extends ManagedInformation{

	private List<QuotaState> quotaState;
	
	public Quota(String userName, String userIdentifier, List<QuotaState> quotaState) {
		super(userName, userIdentifier);
		this.quotaState = quotaState;
	}

	public List<QuotaState> getQuotaState() {
		return quotaState;
	}
	
	public void setQuotaState(List<QuotaState> quotaState) {
		this.quotaState = quotaState;
	}
	
	public boolean isValid(AuthorizationProperties props){
		return false;
		
	}
	
	public boolean substractRequestFromQuota(AuthorizationProperties props){
		return false;
		
	}
}
