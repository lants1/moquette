package org.eclipse.moquette.fce.model.quota;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.moquette.fce.model.ManagedInformation;
import org.eclipse.moquette.fce.model.common.IValid;
import org.eclipse.moquette.fce.service.FceServiceFactory;
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
	
	public UserQuota(String alias, String usernameHash, MqttAction action, List<Quota> quotaStates) {
		super(alias, usernameHash);
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
	public boolean isValid(FceServiceFactory services, AuthorizationProperties props, MqttAction operation){
		for(Quota quota : getQuotas()){
			if(!quota.isValid(services, props, operation)){
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
