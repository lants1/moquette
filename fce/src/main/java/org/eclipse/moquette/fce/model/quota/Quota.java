package org.eclipse.moquette.fce.model.quota;

import org.eclipse.moquette.plugin.AuthorizationProperties;

public abstract class Quota {
	
	QuotaState state;
	
	public Quota(QuotaState state) {
		super();
		this.state = state;
	}

	abstract boolean isValid(AuthorizationProperties prop);

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
