package org.eclipse.moquette.fce.event;

import java.util.logging.Logger;

import org.eclipse.moquette.fce.common.util.FceHashUtil;
import org.eclipse.moquette.fce.service.IFceServiceFactory;
import org.eclipse.moquette.plugin.AuthenticationProperties;

/**
 * Handler which checks authentication.
 * 
 * @author lants1
 *
 */
public class AuthenticationHandler {

	private final static Logger log = Logger.getLogger(FceEventHandler.class.getName());

	final IFceServiceFactory services;
	final String pluginClientIdentifer;

	public AuthenticationHandler(IFceServiceFactory services, String pluginClientIdentifier) {
		this.services = services;
		this.pluginClientIdentifer = pluginClientIdentifier;
	}

	public boolean checkValid(AuthenticationProperties props) {
		log.info("recieved checkValid Event for " + props.getUsername());
		return FceHashUtil.validateClientIdHash(props);
	}
}
