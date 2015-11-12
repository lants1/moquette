package org.eclipse.moquette.fce.job;

import java.util.logging.Logger;

public class ManagedZoneGarbageCollector implements Runnable{

	private final static Logger log = Logger.getLogger(ManagedZoneGarbageCollector.class.getName()); 
	
	@Override
	public void run() {
		// TODO lants1 remove managed zones where nobody is able to manage them...
		
		log.info("JOB: successful completed");
	}

}
