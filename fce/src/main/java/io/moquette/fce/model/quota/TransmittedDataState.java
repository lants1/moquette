package io.moquette.fce.model.quota;

import io.moquette.fce.model.common.DataUnit;
import io.moquette.fce.service.FceServiceFactory;
import io.moquette.plugin.AuthorizationProperties;
import io.moquette.plugin.MqttAction;

/**
 * State of a quota for transmitted data.
 * 
 * @author lants1
 *
 */
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
	public boolean isValid(FceServiceFactory services, AuthorizationProperties props, MqttAction operation) {
		if (getCurrentQuota() + props.getMessage().position() < getMaxQuota()) {
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
		setCurrentQuota(getCurrentQuota()+props.getMessage().limit());
		
	}
	
}