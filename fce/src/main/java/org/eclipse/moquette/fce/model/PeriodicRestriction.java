package org.eclipse.moquette.fce.model;

/**
 * Periodic Restriction
 * 
 * @author lants1
 *
 */
public class PeriodicRestriction extends Restriction{

	private ManagedCycle cyle;

	public ManagedCycle getCyle() {
		return cyle;
	}

	public void setCyle(ManagedCycle cyle) {
		this.cyle = cyle;
	}

}
