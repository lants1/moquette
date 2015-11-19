package org.eclipse.moquette.plugin;

import java.nio.ByteBuffer;

public class AuthorizationProperties {
	private final String topic;
	private final String user; 
	private final String clientId;
	private final Boolean anonymous;
	private final ByteBuffer message;
	
	public AuthorizationProperties(String topic, String user, String clientId, Boolean anonymous, ByteBuffer message) {
		super();
		this.topic = topic;
		this.user = user;
		this.clientId = clientId;
		this.anonymous = anonymous;
		this.message = message;
	}
	
	public String getTopic() {
		return topic;
	}
	
	public String getUser() {
		return user;
	}
	
	public String getClientId() {
		return clientId;
	}
	
	public Boolean getAnonymous() {
		return anonymous;
	}

	public ByteBuffer getMessage() {
		return message;
	}
	
}
