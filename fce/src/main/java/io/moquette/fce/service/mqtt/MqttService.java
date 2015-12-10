package io.moquette.fce.service.mqtt;

import io.moquette.fce.event.internal.IFceMqttCallback;

/**
 * MqttService Methods.
 * 
 * @author lants1
 *
 */
public interface MqttService {

	/**
	 * Publish a retained message (msg) to topic on the broker.
	 * 
	 * @param topic String
	 * @param msg String
	 */
	void publish(String topic, String msg);
	
	/**
	 * Deletes a retained message on topic. (Delete means publish an empty message to the topic)
	 * 
	 * @param topic String
	 */
	void delete(String topic);
	
	/**
	 * Adds a new new subscription on the broker with a callback.
	 * 
	 * @param topic String
	 * @param handler IFceMqttCallback
	 */
	void addNewSubscription(String topic, IFceMqttCallback handler);
}
