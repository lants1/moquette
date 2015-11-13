package org.eclipse.moquette.fce.model.configuration;

import org.eclipse.moquette.fce.common.SizeUnit;

/**
 * Abstract class for all publish or subscribe restrictions with common methods.
 * 
 * @author lants1
 *
 */
public abstract class Restriction {

	private int messageCount;
	private int maxMessageSize;
	private int totalMessageSize;
	private SizeUnit sizeUnit;
	
	public Restriction(int messageCount, int maxMessageSizeKb, int totalMessageSizeKb, SizeUnit sizeUnit) {
		super();
		this.messageCount = messageCount;
		this.maxMessageSize = maxMessageSizeKb;
		this.totalMessageSize = totalMessageSizeKb;
		this.sizeUnit = sizeUnit;
	}
	
	public int getMessageCount() {
		return messageCount;
	}
	public void setMessageCount(int messageCount) {
		this.messageCount = messageCount;
	}
	public int getMaxMessageSizeKb() {
		return maxMessageSize;
	}
	public void setMaxMessageSizeKb(int maxMessageSizeKb) {
		this.maxMessageSize = maxMessageSizeKb;
	}
	public int getTotalMessageSizeKb() {
		return totalMessageSize;
	}
	public void setTotalMessageSizeKb(int totalMessageSizeKb) {
		this.totalMessageSize = totalMessageSizeKb;
	}

	public SizeUnit getSizeUnit() {
		return sizeUnit;
	}

	public void setSizeUnit(SizeUnit sizeUnit) {
		this.sizeUnit = sizeUnit;
	}
}
