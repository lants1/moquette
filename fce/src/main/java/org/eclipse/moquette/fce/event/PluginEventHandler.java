package org.eclipse.moquette.fce.event;

import java.util.logging.Logger;

import org.eclipse.moquette.fce.service.FceServiceFactory;

public class PluginEventHandler {

	private final static Logger log = Logger.getLogger(PluginEventHandler.class.getName());
	
	FceServiceFactory services;

	public PluginEventHandler(FceServiceFactory services) {
		this.services = services;
	}

	public boolean checkValid(String username, byte[] password) {
		log.fine("recieved checkValid Event for " + username);
		// TODO Auto-generated method stub
		return true;
	}

	public boolean canWrite(String topic, String user, String client) {
		log.fine("recieved canWrite Event on " + topic + "from client"+ client);
		return services.getAuthorizationService().getBasicPermission(topic).isWriteable();
	}

	public boolean canRead(String topic, String user, String client) {
		log.fine("recieved canRead Event on " + topic + "from client"+ client);
		return services.getAuthorizationService().getBasicPermission(topic).isReadable();
	}
}
