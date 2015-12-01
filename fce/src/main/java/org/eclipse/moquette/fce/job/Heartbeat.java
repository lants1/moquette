package org.eclipse.moquette.fce.job;

import java.util.logging.Logger;

import org.eclipse.moquette.fce.service.IFceServiceFactory;

/**
 * Heartbeat for the plugin...
 * 
 * @author lants1
 *
 */
public class Heartbeat implements Runnable {

	private final static Logger log = Logger.getLogger(Heartbeat.class.getName());

	private IFceServiceFactory services;

	public Heartbeat(IFceServiceFactory services) {
		super();
		this.services = services;
	}

	@Override
	public void run() {
		log.info("JOB Heartbeat: internal pluginclient connected:"+services.getMqtt().isConnected());
	}

}
