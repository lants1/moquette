package io.moquette.plugin;

/**
 * AuthenticationProperties is a pojo with elements which can be used to authenticate a request.
 * 
 * @author lants1
 *
 */
public class AuthenticationProperties {
	private final String username;
	private final byte[] password; 
	private final String clientId;
	
	/**
	 * Constructor
	 * 
	 * @param username String
	 * @param password byte[]
	 * @param clientId String
	 */
	public AuthenticationProperties(String username, byte[] password, String clientId) {
		super();
		this.username = username;
		this.password = password;
		this.clientId = clientId;
	}

	/**
	 * Username of the client
	 * 
	 * @return String username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Password of the client
	 * 
	 * @return byte[] password
	 */
	public byte[] getPassword() {
		return password;
	}

	/**
	 * Client id of the client
	 * 
	 * @return String clientId
	 */
	public String getClientId() {
		return clientId;
	}
	
}
