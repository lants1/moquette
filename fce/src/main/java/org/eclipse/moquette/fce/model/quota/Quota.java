package org.eclipse.moquette.fce.model.quota;

import org.eclipse.moquette.fce.model.IValid;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.MqttOperation;

public abstract class Quota implements IValid {
	
	QuotaState state;
	
	public Quota(QuotaState state) {
		super();
		this.state = state;
	}

	public abstract boolean isValid(AuthorizationProperties prop, MqttOperation operation);
	
	public void substractRequestFromQuota(AuthorizationProperties prop){
		state.substractRequestFromQuota(prop);
	}

	public QuotaState getState() {
		return state;
	}

	public void setState(QuotaState state) {
		this.state = state;
	}
	
	public void flush(){
		state.flush();
	}
}
