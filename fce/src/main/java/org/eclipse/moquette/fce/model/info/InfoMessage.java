package org.eclipse.moquette.fce.model.info;

import org.eclipse.moquette.fce.model.ManagedInformation;

public class InfoMessage extends ManagedInformation{

	private InfoMessageType messageType;
	private String messageText;
	
	public InfoMessage(String userName, String userIdentifier, InfoMessageType messageType, String messageText) {
		super(userName, userIdentifier);
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
