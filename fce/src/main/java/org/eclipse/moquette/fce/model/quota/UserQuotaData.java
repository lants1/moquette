package org.eclipse.moquette.fce.model.quota;

import java.util.List;

import org.eclipse.moquette.fce.model.IValid;
import org.eclipse.moquette.fce.model.ManagedInformation;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.MqttOperation;

public class UserQuotaData extends ManagedInformation implements IValid{

	private List<Quota> quotas;
	
	public UserQuotaData(String userName, String userIdentifier, List<Quota> quotaStates) {
		super(userName, userIdentifier);
		this.quotas = quotaStates;
	}

	public List<Quota> getQuotas() {
		return quotas;
	}
	
	public void setQuotas(List<Quota> quotas) {
		this.quotas = quotas;
	}

	@Override
	public boolean isValid(AuthorizationProperties props, MqttOperation operation){
		for(Quota quota : quotas){
			if(!quota.isValid(props, operation)){
				return false;
			}
		}
		return true;
	}

	public void substractRequestFromQuota(AuthorizationProperties props, MqttOperation operation){
		for(Quota quota : quotas){
			quota.substractRequestFromQuota(props);
		}
	}

}
