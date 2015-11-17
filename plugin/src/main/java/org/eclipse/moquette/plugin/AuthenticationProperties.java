package org.eclipse.moquette.plugin;

public class AuthenticationProperties {
	private final String username;
	private final byte[] password; 
	private final String clientId;
	
	public AuthenticationProperties(String username, byte[] password, String clientId) {
		super();
		this.username = username;
		this.password = password;
		this.clientId = clientId;
	}

	public String getUsername() {
		return username;
	}

	public byte[] getPassword() {
		return password;
	}

	public String getClientId() {
		return clientId;
	}
	
}
