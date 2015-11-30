package org.eclipse.moquette.plugin;

/**
 * Enum which represents the given operation/action of a request.
 * 
 * @author lants1
 *
 */
public enum MqttAction {
	PUBLISH("publish"), SUBSCRIBE("subscribe");
	
	private String value;

	private MqttAction(String value) {
		this.value = value;
	}

	/**
	 * Gets the action as string.
	 * 
	 * @return action as string
	 */
	public String getValue() {
		return value;
	}
}
