package io.moquette.fce.event.broker;

import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import io.moquette.fce.context.FceContext;
import io.moquette.fce.event.FceEventHandler;
import io.moquette.fce.service.FceServiceFactory;
import io.moquette.plugin.AuthenticationProperties;

/**
 * Handler which checks authentication.
 * 
 * @author lants1
 *
 */
public class AuthenticationHandler {

	private final static Logger log = Logger.getLogger(FceEventHandler.class.getName());

	final FceContext context;
	final FceServiceFactory services;

	public AuthenticationHandler(FceContext context, FceServiceFactory services) {
		this.services = services;
		this.context = context;
	}

	public boolean checkValid(AuthenticationProperties props) {
		log.info("recieved checkValid Event for " + props.getUsername());

		// Allow anonymous access / anonymous flag is setted...
		if (StringUtils.isEmpty(props.getUsername()) && props.getPassword() == null) {
			context.getHashAssignment().remove(props.getClientId());
			return true;
		}

		boolean usernameHashValidation = services.getHashing().validateUsernameHash(props);
		if (usernameHashValidation) {
			// if a user with a valid hash login and a user with the same clientid already exists the other session is dropped by the broker...
			context.getHashAssignment().put(props.getClientId(), services.getHashing().generateHash(props.getUsername()));
		}
		
		// if the plugin is active only users with usr:username, pw:hash(username) could login to the broker
		return usernameHashValidation;
	}
}
