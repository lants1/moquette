package org.eclipse.moquette.fce.model;

import java.util.Date;

/**
 * Abstract class for every ManagedInformation and it's common fields.
 * 
 * @author lants1
 *
 */
public abstract class ManagedInformation {

	private String userName;
	private String userIdentifier;
	private Date timestamp;
	
	public ManagedInformation(String userName, String userIdentifier) {
		super();
		this.userName = userName;
		this.userIdentifier = userIdentifier;
		this.timestamp = new Date();
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserIdentifier() {
		return userIdentifier;
	}

	public void setUserIdentifier(String userIdentifier) {
		this.userIdentifier = userIdentifier;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
}
