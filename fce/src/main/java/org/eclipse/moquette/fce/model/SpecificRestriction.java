package org.eclipse.moquette.fce.model;

import java.util.Date;

/**
 * Timebased restriction from date to a date.
 * 
 * @author lants1
 *
 */
public class SpecificRestriction extends Restriction {

	public Date from;
	public Date to;

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
