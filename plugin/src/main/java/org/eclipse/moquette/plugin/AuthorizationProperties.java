package org.eclipse.moquette.plugin;

import java.nio.ByteBuffer;

/**
 * AuthorizationProperties is a pojo for authentication properties which can be used to authorize a request.
 * 
 * @author lants1
 *
 */
public class AuthorizationProperties {
	private final String topic;
	private final String user; 
	private final String clientId;
	private final Boolean anonymous;
	private final ByteBuffer message;
	
	/**
	 * Constructor.
	 * 
	 * @param String topic
	 * @param String user
	 * @param String clientId
	 * @param Boolean anonymous
	 * @param ByteBuffer message
	 */
	public AuthorizationProperties(String topic, String user, String clientId, Boolean anonymous, ByteBuffer message) {
		super();
		this.topic = topic;
		this.user = user;
		this.clientId = clientId;
		this.anonymous = anonymous;
		this.message = message;
	}
	
	/**
	 * Get the topic identifier.
	 * 
	 * @return String topicIdentifier of the request
	 */
	public String getTopic() {
		return topic;
	}
	
	/**
	 * Username
	 * 
	 * @return String user
	 */
	public String getUser() {
		return user;
	}
	
	/**
	 * ClientId
	 * 
	 * @return String clientId
	 */
	public String getClientId() {
		return clientId;
	}
	
	/**
	 * Is set when a clientid has no authentication data, anonymous login without username or password
	 * @return Boolean anonymous flag true if it's a anonymous login
	 */
	public Boolean getAnonymous() {
		return anonymous;
	}

	/**
	 * The message payload of a request.
	 * 
	 * @return ByteBuffer message
	 */
	public ByteBuffer getMessage() {
		return message;
	}
	
}
