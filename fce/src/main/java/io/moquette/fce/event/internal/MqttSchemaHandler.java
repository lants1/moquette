package io.moquette.fce.event.internal;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import io.moquette.fce.context.FceContext;
import io.moquette.fce.event.FceEventHandler;
import io.moquette.fce.exception.FceSystemException;
import io.moquette.fce.service.FceServiceFactory;
import io.moquette.plugin.AuthorizationProperties;
import io.moquette.plugin.MqttAction;

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