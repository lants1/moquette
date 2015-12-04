package io.moquette.fce.event.broker;

import java.util.logging.Logger;

import io.moquette.fce.context.FceContext;
import io.moquette.fce.event.FceEventHandler;
import io.moquette.fce.service.FceServiceFactory;
import io.moquette.plugin.AuthorizationProperties;
import io.moquette.plugin.MqttAction;

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
