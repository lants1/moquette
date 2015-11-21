package org.eclipse.moquette.fce.model.configuration;

import org.eclipse.moquette.fce.common.DataUnit;
import org.eclipse.moquette.fce.common.XmlSchemaValidationUtil;
import org.eclipse.moquette.fce.model.IValid;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.MqttAction;

/**
 * Abstract class for all publish or subscribe restrictions with common methods.
 * 
 * @author lants1
 *
 */
public abstract class Restriction implements IValid {

	private int messageCount;
	private int maxMessageSize;
	private int totalMessageSize;
	private DataUnit sizeUnit;
	private String wsdlUrl;

	public Restriction(int messageCount, int maxMessageSizeKb, int totalMessageSizeKb, DataUnit sizeUnit,
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

	public DataUnit getDataUnit() {
		return sizeUnit;
	}

	public void setSizeUnit(DataUnit sizeUnit) {
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

	public boolean isValidCommon(AuthorizationProperties props, MqttAction operation){
		if(getMaxMessageSize() > 0){
			if(!(props.getMessage().position() < (getMaxMessageSize() * sizeUnit.getMultiplier()))){
				return false;
			}
		}
		
		if (!getWsdlUrl().isEmpty()) {
			if (!XmlSchemaValidationUtil
					.isValidXmlFileAccordingToSchema(props.getMessage(), getWsdlUrl())) {
				return false;
			}
		}
		
		return true;
		
	}
}
