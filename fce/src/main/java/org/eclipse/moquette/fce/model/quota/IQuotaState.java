package org.eclipse.moquette.fce.model.quota;

import org.eclipse.moquette.fce.model.common.IValid;
import org.eclipse.moquette.plugin.AuthorizationProperties;

/**
 * Inteface for quota states and it's mandatory methods.
 * 
 * @author lants1
 *
 */
public interface IQuotaState extends IValid{

	/**
	 * sets the quotastate to "0", initial state.
	 */
	void flush();
	
	/**
	 * substract a request from the quota state
	 * 
	 * @param AuthorizationProperties props
	 */
	void substractRequestFromQuota(AuthorizationProperties props);
	
}
