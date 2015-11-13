package org.eclipse.moquette.fce.model.info;

public class InfoMessage {

	private InfoMessageType messageType;
	private String messageText;
	
	public InfoMessage(InfoMessageType messageType, String messageText) {
		super();
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
