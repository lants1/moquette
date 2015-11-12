package org.eclipse.moquette.fce.job;

import java.util.logging.Logger;

public class PeriodicQuotaCleaner implements Runnable{
	
	private final static Logger log = Logger.getLogger(PeriodicQuotaCleaner.class.getName());
	
	@Override
	public void run() {
		// TODO lants1 reset quota counters according to cycle intervall
		// is called every hour needs to check which intervall is elapsed
		
		log.info("JOB: completed succefully");
	}
}
