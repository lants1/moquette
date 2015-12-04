package io.moquette.fce.event.internal;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import io.moquette.fce.context.FceContext;
import io.moquette.fce.service.FceServiceFactory;

public interface IFceMqttCallback {
	
	void injectFceEnvironment(FceContext context, FceServiceFactory services);
	
	void messageArrived(String topicIdentifier, MqttMessage message) throws Exception;
}
