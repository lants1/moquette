package org.eclipse.moquette.fce.job;

import java.util.logging.Logger;

import org.eclipse.moquette.fce.context.FceContext;
import org.eclipse.moquette.fce.service.FceServiceFactory;

/**
 * Heartbeat for the plugin...
 * 
 * @author lants1
 *
 */
public class Heartbeat implements Runnable {

	private final static Logger log = Logger.getLogger(Heartbeat.class.getName());

	private FceServiceFactory services;
	private FceContext context;
	
	public Heartbeat(FceContext context, FceServiceFactory services) {
		super();
		this.services = services;
		this.context = context;
	}

	@Override
	public void run() {
		log.info("JOB Heartbeat: internal pluginclient connected:"+services.getMqtt().isConnected());
	}

}
