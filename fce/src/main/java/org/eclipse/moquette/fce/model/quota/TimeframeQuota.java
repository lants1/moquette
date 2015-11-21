package org.eclipse.moquette.fce.model.quota;

import java.util.Date;

import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.MqttOperation;

public class TimeframeQuota extends Quota {

	private Date from;
	private Date to;

	public TimeframeQuota(Date from, Date to, QuotaState state) {
		super(state);
		this.from = from;
		this.to = to;
	}

	public Date getFrom() {
		return from;
	}

	public void setFrom(Date from) {
		this.from = from;
	}

	public Date getTo() {
		return to;
	}

	public void setTo(Date to) {
		this.to = to;
	}

	@Override
	public boolean isValid(AuthorizationProperties props, MqttOperation operation) {
		Date now = new Date();
		if (now.after(from) && now.before(to)) {
			getState().isValid(props, operation);
		}

		return false;
	}
}
