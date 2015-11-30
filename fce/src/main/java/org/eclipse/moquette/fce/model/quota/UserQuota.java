package org.eclipse.moquette.fce.model.quota;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.moquette.fce.model.ManagedInformation;
import org.eclipse.moquette.fce.model.common.IValid;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.MqttAction;

/**
 * Contains all Quotas for an user.
 * 
 * @author lants1
 *
 */
public class UserQuota extends ManagedInformation implements IValid{

	private final MqttAction action;
	private List<Quota> quotas;
	
	public UserQuota(String userName, String userIdentifier, MqttAction action, List<Quota> quotaStates) {
		super(userName, userIdentifier);
		this.quotas = quotaStates;
		this.action = action;
	}

	public List<Quota> getQuotas() {
		if(quotas == null || quotas.isEmpty()){
			return new ArrayList<>();
		}
		return quotas;
	}
	
	public MqttAction getAction() {
		return action;
	}

	public void setQuotas(List<Quota> quotas) {
		this.quotas = quotas;
	}

	@Override
	public boolean isValid(AuthorizationProperties props, MqttAction operation){
		for(Quota quota : getQuotas()){
			if(!quota.isValid(props, operation)){
				return false;
			}
		}
		return true;
	}

	public void substractRequestFromQuota(AuthorizationProperties props, MqttAction operation){
		setTimestamp(new Date());
		for(Quota quota : getQuotas()){
			quota.substractRequestFromQuota(props);
		}
	}

}
