package org.eclipse.moquette.fce.model.quota;

import org.eclipse.moquette.fce.model.IValid;
import org.eclipse.moquette.plugin.AuthorizationProperties;

public interface IQuotaState extends IValid{

	void flush();
	
	void substractRequestFromQuota(AuthorizationProperties props);
	
}
