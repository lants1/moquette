package io.moquette.fce.event.internal;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import io.moquette.fce.context.FceContext;
import io.moquette.fce.service.FceServiceFactory;

/**
 * Internal FCE callback interface.
 * 
 * @author lants1
 *
 */
public interface IFceMqttCallback {
	
	/**
	 * Injects context and services after the callback is constructed.
	 * 
	 * @param context
	 * @param services
	 */
	void injectFceEnvironment(FceContext context, FceServiceFactory services);
	
	/**
	 * Called when a message arrives from a subcribed topic.
	 * 
	 * @param topicIdentifier sTRING
	 * @param message MqttMessage
	 * @throws Exception
	 */
	void messageArrived(String topicIdentifier, MqttMessage message) throws Exception;
}
