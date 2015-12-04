package org.eclipse.moquette.fce.model.quota;

import org.eclipse.moquette.fce.common.DataUnit;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.MqttAction;

public class TransmittedDataState implements IQuotaState {

	private int maxQuota;
	private int currentQuota;

	private DataUnit sizeUnit;

	public TransmittedDataState(int maxQuota, int currentQuota) {
		super();
		this.maxQuota = maxQuota;
		this.currentQuota = currentQuota;
		this.sizeUnit = DataUnit.B;
	}

	public int getMaxQuota() {
		return maxQuota * sizeUnit.getMultiplier();
	}

	public void setMaxQuota(int maxQuota) {
		this.maxQuota = maxQuota;
	}

	public int getCurrentQuota() {
		return currentQuota * sizeUnit.getMultiplier();
	}

	public void setCurrentQuota(int currentQuota) {
		this.currentQuota = currentQuota;
	}

	public DataUnit getSizeUnit() {
		return sizeUnit;
	}

	public void setSizeUnit(DataUnit sizeUnit) {
		this.sizeUnit = sizeUnit;
	}

	@Override
	public boolean isValid(AuthorizationProperties props, MqttAction operation) {
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