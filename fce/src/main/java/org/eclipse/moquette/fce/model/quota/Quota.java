package org.eclipse.moquette.fce.model.quota;

import org.eclipse.moquette.fce.model.IValid;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.MqttAction;

public abstract class Quota implements IValid {
	
	IQuotaState state;
	
	public Quota(IQuotaState state) {
		super();
		this.state = state;
	}

	public abstract boolean isValid(AuthorizationProperties prop, MqttAction operation);
	
	public void substractRequestFromQuota(AuthorizationProperties prop){
		state.substractRequestFromQuota(prop);
	}

	public IQuotaState getState() {
		return state;
	}

	public void setState(IQuotaState state) {
		this.state = state;
	}
	
	public void flush(){
		state.flush();
	}
}
