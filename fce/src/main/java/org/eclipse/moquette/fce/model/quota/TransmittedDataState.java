package org.eclipse.moquette.fce.model.quota;

import org.eclipse.moquette.fce.common.SizeUnit;
import org.eclipse.moquette.plugin.AuthorizationProperties;

public class TransmittedDataState implements QuotaState {

	private int maxQuota;
	private int currentQuota;

	private SizeUnit sizeUnit;

	public TransmittedDataState(int maxQuota, int currentQuota, SizeUnit sizeUnit) {
		super();
		this.maxQuota = maxQuota;
		this.currentQuota = currentQuota;
		this.sizeUnit = sizeUnit;

		if (sizeUnit == null) {
			sizeUnit = SizeUnit.B;
		}
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
	public boolean isValid(AuthorizationProperties props) {
		if (this.getCurrentQuota() + props.getMessage().position() < this.getMaxQuota()) {
			return true;
		}
		return false;
	}

	@Override
	public void flush() {
		setCurrentQuota(0);
	}
}