package org.eclipse.moquette.fce.model;

/**
 * Abstract class for all publish or subscribe restrictions with common methods.
 * 
 * @author lants1
 *
 */
public abstract class Restriction {

	private int messageCount;
	private int maxMessageSizeKb;
	private int totalMessageSizeKb;
	
	public int getMessageCount() {
		return messageCount;
	}
	public void setMessageCount(int messageCount) {
		this.messageCount = messageCount;
	}
	public int getMaxMessageSizeKb() {
		return maxMessageSizeKb;
	}
	public void setMaxMessageSizeKb(int maxMessageSizeKb) {
		this.maxMessageSizeKb = maxMessageSizeKb;
	}
	public int getTotalMessageSizeKb() {
		return totalMessageSizeKb;
	}
	public void setTotalMessageSizeKb(int totalMessageSizeKb) {
		this.totalMessageSizeKb = totalMessageSizeKb;
	}
}
