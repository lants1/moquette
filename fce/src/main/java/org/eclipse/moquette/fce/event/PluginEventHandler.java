package org.eclipse.moquette.fce.event;

import java.util.logging.Logger;

import org.eclipse.moquette.fce.common.FceHashUtil;
import org.eclipse.moquette.fce.service.FceServiceFactory;
import org.eclipse.moquette.plugin.AuthenticationProperties;
import org.eclipse.moquette.plugin.AuthorizationProperties;

public class PluginEventHandler {

	private final static Logger log = Logger.getLogger(PluginEventHandler.class.getName());
	
	FceServiceFactory services;

	public PluginEventHandler(FceServiceFactory services) {
		this.services = services;
	}

	public boolean checkValid(AuthenticationProperties props) {
		// TODO Lan is this really a good idea?
		// dammit if sombody else login with the same username and some pw the other one is blocked...
		// The Server MUST allow ClientIds which are between 1 and 23 UTF-8 encoded bytes in length, and that contain only the characters
		// "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
		// is it a good idea to define
		log.fine("recieved checkValid Event for " + props.getUsername());
		
		return FceHashUtil.validateClientIdHash(props);
	}

	public boolean canWrite(AuthorizationProperties properties) {
		log.fine("recieved canWrite Event on " + properties.getTopic() + "from client"+ properties.getClientId());
		return services.getAuthorizationService().getBasicPermission(properties.getTopic()).isWriteable();
	}

	public boolean canRead(AuthorizationProperties properties) {
		log.fine("recieved canRead Event on " + properties.getTopic() + "from client"+ properties.getClientId());
		return services.getAuthorizationService().getBasicPermission(properties.getTopic()).isReadable();
	}
}
