package org.eclipse.moquette.fce.model.configuration;

import java.util.Date;

import org.eclipse.moquette.fce.common.SizeUnit;

/**
 * Timebased restriction from date to a date.
 * 
 * @author lants1
 *
 */
public class SpecificRestriction extends Restriction {

	private Date from;
	private Date to;

	public SpecificRestriction(Date from, Date to, int messageCount, int maxMessageSize, int totalMessageSize, SizeUnit sizeUnit, String wsdlUrl) {
		super(messageCount, maxMessageSize, totalMessageSize, sizeUnit, wsdlUrl);
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

}
