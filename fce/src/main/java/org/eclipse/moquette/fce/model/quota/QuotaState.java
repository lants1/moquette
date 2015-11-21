package org.eclipse.moquette.fce.model.quota;

import org.eclipse.moquette.plugin.AuthorizationProperties;

public interface QuotaState {

	boolean isValid(AuthorizationProperties props);
	
	void flush();
	
	void substractRequestFromQuota(AuthorizationProperties props);
	
}
