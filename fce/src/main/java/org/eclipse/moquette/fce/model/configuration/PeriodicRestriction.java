package org.eclipse.moquette.fce.model.configuration;

import org.eclipse.moquette.fce.common.SizeUnit;
import org.eclipse.moquette.fce.model.ManagedCycle;

/**
 * Periodic Restriction
 * 
 * @author lants1
 *
 */
public class PeriodicRestriction extends Restriction{

	private ManagedCycle cyle;

	public PeriodicRestriction(ManagedCycle cycle, int messageCount, int maxMessageSizeKb, int totalMessageSizeKb, SizeUnit sizeUnit) {
		super(messageCount, maxMessageSizeKb, totalMessageSizeKb, sizeUnit);
		this.cyle = cycle;
	}

	public ManagedCycle getCyle() {
		return cyle;
	}

	public void setCyle(ManagedCycle cyle) {
		this.cyle = cyle;
	}

}
