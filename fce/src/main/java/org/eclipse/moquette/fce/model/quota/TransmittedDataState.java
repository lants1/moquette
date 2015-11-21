package org.eclipse.moquette.fce.model.quota;

import org.eclipse.moquette.fce.common.SizeUnit;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.MqttOperation;

public class TransmittedDataState implements QuotaState {

	private int maxQuota;
	private int currentQuota;

	private SizeUnit sizeUnit;

	public TransmittedDataState(int maxQuota, int currentQuota) {
		super();
		this.maxQuota = maxQuota;
		this.currentQuota = currentQuota;
		this.sizeUnit = SizeUnit.B;
	}

	public int getMaxQuota() {
		return maxQuota * sizeUnit.getMultiplikator();
	}

	public void setMaxQuota(int maxQuota) {
		this.maxQuota = maxQuota;
	}

	public int getCurrentQuota() {
		return currentQuota * sizeUnit.getMultiplikator();
	}

	public void setCurrentQuota(int currentQuota) {
		this.currentQuota = currentQuota;
	}

	public SizeUnit getSizeUnit() {
		return sizeUnit;
	}

	public void setSizeUnit(SizeUnit sizeUnit) {
		this.sizeUnit = sizeUnit;
	}

	@Override
	public boolean isValid(AuthorizationProperties props, MqttOperation operation) {
		if (this.getCurrentQuota() + props.getMessage().position() < this.getMaxQuota()) {
			return true;
		}
		return false;
	}

	@Override
	public void flush() {
		setCurrentQuota(0);
	}

	@Override
	public void substractRequestFromQuota(AuthorizationProperties props) {
		this.setCurrentQuota(getCurrentQuota()+props.getMessage().position());
		
	}
	
}