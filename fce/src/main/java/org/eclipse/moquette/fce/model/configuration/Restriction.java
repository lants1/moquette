package org.eclipse.moquette.fce.model.configuration;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.moquette.fce.common.util.XmlSchemaValidationUtil;
import org.eclipse.moquette.fce.model.common.DataUnit;
import org.eclipse.moquette.fce.model.common.IValid;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.MqttAction;

/**
 * Abstract class for all publish or subscribe restrictions with common methods.
 * 
 * @author lants1
 *
 */
public abstract class Restriction implements IValid {

	private final FceAction mqttAction;
	private final int messageCount;
	private final int maxMessageSize;
	private final int totalMessageSize;
	private final DataUnit sizeUnit;
	private final String wsdlUrl;

	public Restriction(FceAction mqttAction, int messageCount, int maxMessageSizeKb, int totalMessageSizeKb, DataUnit sizeUnit,
			String wsdlUrl) {
		super();
		this.messageCount = messageCount;
		this.maxMessageSize = maxMessageSizeKb;
		this.totalMessageSize = totalMessageSizeKb;
		this.sizeUnit = sizeUnit;
		this.wsdlUrl = wsdlUrl;
		this.mqttAction = mqttAction;
	}

	public int getMessageCount() {
		return messageCount;
	}


	public DataUnit getDataUnit() {
		return sizeUnit;
	}

	public String getWsdlUrl() {
		return wsdlUrl;
	}

	public int getMaxMessageSize() {
		return maxMessageSize;
	}

	public int getTotalMessageSize() {
		return totalMessageSize;
	}
	
	public FceAction getMqttAction() {
		return mqttAction;
	}

	public DataUnit getSizeUnit() {
		return sizeUnit;
	}

	public boolean isValidCommon(AuthorizationProperties props, MqttAction operation){
		if(getMaxMessageSize() > 0){
			if(!(props.getMessage().position() < (getMaxMessageSize() * getSizeUnit().getMultiplier()))){
				return false;
			}
		}
		
		if (StringUtils.isNotEmpty(getWsdlUrl())) {
			if (!XmlSchemaValidationUtil
					.isValidXmlFileAccordingToSchema(props.getMessage(), getWsdlUrl())) {
				return false;
			}
		}
		
		return true;
		
	}
}
