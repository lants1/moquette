package org.eclipse.moquette.fce.model;

public abstract class ManagedInformation {

	private String userName;
	private String userIdentifier;
	
	public ManagedInformation(String userName, String userIdentifier) {
		super();
		this.userName = userName;
		this.userIdentifier = userIdentifier;
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
	
	
	
}
