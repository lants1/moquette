package org.eclipse.moquette.fce.event;

import java.util.logging.Logger;

import org.eclipse.moquette.fce.context.FceContext;
import org.eclipse.moquette.fce.exception.FceSystemException;
import org.eclipse.moquette.fce.service.FceServiceFactory;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.MqttAction;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttSchemaEventHandler extends FceEventHandler implements MqttCallback {

	private final static Logger log = Logger.getLogger(MqttSchemaEventHandler.class.getName());

	public MqttSchemaEventHandler(FceContext context, FceServiceFactory services) {
		super(context, services);
	}

	@Override
	public void connectionLost(Throwable arg0) {
		try {
			log.warning("internal plugin mqttclient conection to broker connection lost");
			getServices().getMqtt().connect();
		} catch (MqttException e) {
			log.severe("internal plugin mqttclient could not reconnect to broker");
			throw new FceSystemException(e);
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// doNothing
	}

	@Override
	public void messageArrived(String topicIdentifier, MqttMessage message) throws Exception {
		log.info("received internal message for topic:" + topicIdentifier);
		getContext().getSchemaAssignment().put(topicIdentifier, message.getPayload());
	}

	@Override
	public boolean canDoOperation(AuthorizationProperties properties, MqttAction operation) {
		throw new FceSystemException("not implemented");
	}

}