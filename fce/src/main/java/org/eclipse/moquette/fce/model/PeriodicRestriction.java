package org.eclipse.moquette.fce.model;

/**
 * Periodic Restriction
 * 
 * @author lants1
 *
 */
public class PeriodicRestriction extends Restriction{

	private ManagedCycle cyle;

	public PeriodicRestriction(ManagedCycle cycle, int messageCount, int maxMessageSizeKb, int totalMessageSizeKb) {
		super(messageCount, maxMessageSizeKb, totalMessageSizeKb);
		this.cyle = cycle;
	}

	public ManagedCycle getCyle() {
		return cyle;
	}

	public void setCyle(ManagedCycle cyle) {
		this.cyle = cyle;
	}

}
