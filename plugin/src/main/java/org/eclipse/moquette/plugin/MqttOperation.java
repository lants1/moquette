package org.eclipse.moquette.plugin;

public enum MqttOperation {
	PUBLISH("publish"), SUBSCRIBE("subscribe");
	
	private String value;

	private MqttOperation(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
