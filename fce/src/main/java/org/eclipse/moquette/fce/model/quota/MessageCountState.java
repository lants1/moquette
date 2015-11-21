package org.eclipse.moquette.fce.model.quota;

import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.MqttOperation;

public class MessageCountState implements QuotaState {

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
	public boolean isValid(AuthorizationProperties props, MqttOperation operation) {
		// TODO Auto-generated method stub
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
