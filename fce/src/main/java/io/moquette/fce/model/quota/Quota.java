package io.moquette.fce.model.quota;

import io.moquette.fce.model.common.IValid;
import io.moquette.fce.service.FceServiceFactory;
import io.moquette.plugin.AuthorizationProperties;
import io.moquette.plugin.MqttAction;

/**
 * Abstract class for every quota type.
 * 
 * @author lants1
 *
 */
public abstract class Quota implements IValid {
	
	IQuotaState state;
	
	public Quota(IQuotaState state) {
		super();
		this.state = state;
	}

	public abstract boolean isValid(FceServiceFactory services, AuthorizationProperties prop, MqttAction operation);
	
	public void substractRequestFromQuota(AuthorizationProperties prop){
		getState().substractRequestFromQuota(prop);
	}

	public IQuotaState getState() {
		return state;
	}

	public void setState(IQuotaState state) {
		this.state = state;
	}
	
	public void flush(){
		getState().flush();
	}
}
