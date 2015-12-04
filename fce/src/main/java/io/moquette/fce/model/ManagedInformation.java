package io.moquette.fce.model;

import java.util.Date;

/**
 * Abstract class for every ManagedInformation and it's common fields.
 * 
 * @author lants1
 *
 */
public abstract class ManagedInformation {

	private String alias;
	private String userHash;
	private Date timestamp;
	
	public ManagedInformation(String alias, String userHash) {
		super();
		this.alias = alias;
		this.userHash = userHash;
		this.timestamp = new Date();
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getUserHash() {
		return userHash;
	}

	public void setUserHash(String userHash) {
		this.userHash = userHash;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
}
