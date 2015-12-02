package org.eclipse.moquette.fce.model.info;

import org.eclipse.moquette.fce.model.ManagedInformation;

/**
 * Model for a managed info message. 
 * 
 * @author lants1
 *
 */
public class InfoMessage extends ManagedInformation{

	private InfoMessageType messageType;
	private String messageText;
	
	public InfoMessage(String alias, String usernameHash, InfoMessageType messageType, String messageText) {
		super(alias, usernameHash);
		this.messageType = messageType;
		this.messageText = messageText;
	}
	
	public InfoMessageType getMessageType() {
		return messageType;
	}
	public void setMessageType(InfoMessageType messageType) {
		this.messageType = messageType;
	}
	public String getMessageText() {
		return messageText;
	}
	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}
	
	
}
