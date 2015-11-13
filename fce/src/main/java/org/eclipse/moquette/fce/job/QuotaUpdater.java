package org.eclipse.moquette.fce.job;

import java.util.logging.Logger;

import org.eclipse.moquette.fce.service.FceServiceFactory;

public class QuotaUpdater implements Runnable{
	
	private final static Logger log = Logger.getLogger(QuotaUpdater.class.getName());
	
	private FceServiceFactory services;
	
	public QuotaUpdater(FceServiceFactory services) {
		super();
		this.services = services;
	}

	@Override
	public void run() {
		// TODO lants1 reset quota counters according to cycle intervall
		// is called every hour needs to check which intervall is elapsed
		
		log.info("JOB: completed succefully");
	}
}
