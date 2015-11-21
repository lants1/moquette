package org.eclipse.moquette.plugin;

public enum MqttAction {
	PUBLISH("publish"), SUBSCRIBE("subscribe");
	
	private String value;

	private MqttAction(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
