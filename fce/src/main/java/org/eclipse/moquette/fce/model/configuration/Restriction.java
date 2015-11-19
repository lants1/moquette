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
	private String wsdlUrl;

	public Restriction(int messageCount, int maxMessageSizeKb, int totalMessageSizeKb, SizeUnit sizeUnit,
			String wsdlUrl) {
		super();
		this.messageCount = messageCount;
		this.maxMessageSize = maxMessageSizeKb;
		this.totalMessageSize = totalMessageSizeKb;
		this.sizeUnit = sizeUnit;
		this.wsdlUrl = wsdlUrl;
	}

	public int getMessageCount() {
		return messageCount;
	}

	public void setMessageCount(int messageCount) {
		this.messageCount = messageCount;
	}

	public SizeUnit getSizeUnit() {
		return sizeUnit;
	}

	public void setSizeUnit(SizeUnit sizeUnit) {
		this.sizeUnit = sizeUnit;
	}

	public String getWsdlUrl() {
		return wsdlUrl;
	}

	public void setWsdlUrl(String wsdlUrl) {
		this.wsdlUrl = wsdlUrl;
	}

	public int getMaxMessageSize() {
		return maxMessageSize;
	}

	public void setMaxMessageSize(int maxMessageSize) {
		this.maxMessageSize = maxMessageSize;
	}

	public int getTotalMessageSize() {
		return totalMessageSize;
	}

	public void setTotalMessageSize(int totalMessageSize) {
		this.totalMessageSize = totalMessageSize;
	}

}
