package org.eclipse.moquette.fce.event.internal;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

import org.eclipse.moquette.fce.context.FceContext;
import org.eclipse.moquette.fce.event.FceEventHandler;
import org.eclipse.moquette.fce.exception.FceSystemException;
import org.eclipse.moquette.fce.service.FceServiceFactory;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.MqttAction;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttSchemaHandler extends FceEventHandler implements IFceMqttCallback {

	private final static Logger log = Logger.getLogger(MqttSchemaHandler.class.getName());

	public MqttSchemaHandler() {
		super(null, null);
	}
	
	public MqttSchemaHandler(FceContext context, FceServiceFactory services) {
		super(context, services);
	}

	@Override
	public void messageArrived(String topicIdentifier, MqttMessage message) throws Exception {
		log.info("received internal message for topic:" + topicIdentifier);
		getContext().getSchemaAssignment().put(topicIdentifier, ByteBuffer.wrap(message.getPayload()));
	}

	@Override
	public boolean canDoOperation(AuthorizationProperties properties, MqttAction operation) {
		throw new FceSystemException("not implemented");
	}

}