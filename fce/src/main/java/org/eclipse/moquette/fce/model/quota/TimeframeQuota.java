package org.eclipse.moquette.fce.model.quota;

import java.util.Date;

import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.MqttAction;

/**
 * Quota from a date to a date.
 * 
 * @author lants1
 *
 */
public class TimeframeQuota extends Quota {

	private Date from;
	private Date to;

	public TimeframeQuota(Date from, Date to, IQuotaState state) {
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
	public boolean isValid(AuthorizationProperties props, MqttAction operation) {
		Date now = new Date();
		if (now.after(from) && now.before(to)) {
			return getState().isValid(props, operation);
		}

		return false;
	}
}
