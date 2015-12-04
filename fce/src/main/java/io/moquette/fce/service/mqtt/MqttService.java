package io.moquette.fce.service.mqtt;

import io.moquette.fce.event.internal.IFceMqttCallback;

public interface MqttService {

	void publish(String topic, String json);
	
	void delete(String topic);
	
	void addNewSubscription(String topic, IFceMqttCallback handler);
}
