package org.eclipse.moquette.fce.model.configuration;

import java.util.Date;

import org.eclipse.moquette.fce.common.DataUnit;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.MqttAction;

/**
 * Timebased restriction from date to a date.
 * 
 * @author lants1
 *
 */
public class TimeframeRestriction extends Restriction {

	private Date from;
	private Date to;

	public TimeframeRestriction(FceAction mqttAction, Date from, Date to, int messageCount, int maxMessageSize, int totalMessageSize, DataUnit sizeUnit, String wsdlUrl) {
		super(mqttAction, messageCount, maxMessageSize, totalMessageSize, sizeUnit, wsdlUrl);
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
			isValidCommon(props, operation);
		}

		return false;
	}
	
}
