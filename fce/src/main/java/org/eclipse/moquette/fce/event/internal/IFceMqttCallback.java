package org.eclipse.moquette.fce.event.internal;

import org.eclipse.moquette.fce.context.FceContext;
import org.eclipse.moquette.fce.service.FceServiceFactory;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public interface IFceMqttCallback {
	
	void injectFceEnvironment(FceContext context, FceServiceFactory services);
	
	void messageArrived(String topicIdentifier, MqttMessage message) throws Exception;
}
