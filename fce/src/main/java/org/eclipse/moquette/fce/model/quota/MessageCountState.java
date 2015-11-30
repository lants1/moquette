package org.eclipse.moquette.fce.model.quota;

import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.MqttAction;

/**
 * State of a Quota for count of transmitted messages.
 * 
 * @author lants1
 *
 */
public class MessageCountState implements IQuotaState {

	private int maxQuotaCount;
	private int currentQuotaCount;

	public MessageCountState(int maxQuotaCount, int currentQuotaCount) {
		super();
		this.maxQuotaCount = maxQuotaCount;
		this.currentQuotaCount = currentQuotaCount;
	}

	public int getMaxQuotaCount() {
		return maxQuotaCount;
	}

	public void setMaxQuotaCount(int maxQuotaCount) {
		this.maxQuotaCount = maxQuotaCount;
	}

	public int getCurrentQuotaCount() {
		return currentQuotaCount;
	}

	public void setCurrentQuotaCount(int currentQuotaCount) {
		this.currentQuotaCount = currentQuotaCount;
	}

	@Override
	public boolean isValid(AuthorizationProperties props, MqttAction operation) {
		if(maxQuotaCount >= currentQuotaCount+1){
			return true;
		}
		return false;
	}

	@Override
	public void flush() {
		setCurrentQuotaCount(0);
	}

	@Override
	public void substractRequestFromQuota(AuthorizationProperties props) {
		setCurrentQuotaCount(getCurrentQuotaCount()+1);
	}

}
