package org.eclipse.moquette.plugin;

public class AuthorizationProperties {
	private final String topic;
	private final String user; 
	private final String clientId;
	private final Boolean anonymous;
	
	public AuthorizationProperties(String topic, String user, String clientId, Boolean anonymous) {
		super();
		this.topic = topic;
		this.user = user;
		this.clientId = clientId;
		this.anonymous = anonymous;
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
	
}
