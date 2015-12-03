package org.eclipse.moquette.fce.event.broker;

import java.util.logging.Logger;

import org.eclipse.moquette.fce.context.FceContext;
import org.eclipse.moquette.fce.event.FceEventHandler;
import org.eclipse.moquette.fce.service.FceServiceFactory;
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
	
	public UnmanagedTopicHandler(FceContext context, FceServiceFactory services) {
		super(context, services);
	}

	@Override
	public boolean canDoOperation(AuthorizationProperties properties, MqttAction operation) {
		log.info("unmanaged action:"+operation +  " for topic:"+properties.getTopic()+ " received");
		return true;
	}

}
