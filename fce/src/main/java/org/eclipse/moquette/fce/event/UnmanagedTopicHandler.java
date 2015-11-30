package org.eclipse.moquette.fce.event;

import java.util.logging.Logger;

import org.eclipse.moquette.fce.service.IFceServiceFactory;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.MqttAction;

/**
 * Unmanaged Handler when no Managed Zone is involved....
 * 
 * @author lants1
 *
 */
public class UnmanagedTopicHandler extends FceEventHandler {

	private final static Logger log = Logger.getLogger(UnmanagedTopicHandler.class.getName());
	
	public UnmanagedTopicHandler(IFceServiceFactory services, String pluginClientIdentifier) {
		super(services, pluginClientIdentifier);
	}

	@Override
	public boolean canDoOperation(AuthorizationProperties properties, MqttAction operation) {
		log.info("unmanaged action:"+operation +  " for topic:"+properties.getTopic()+ " received");
		return true;
	}

}
