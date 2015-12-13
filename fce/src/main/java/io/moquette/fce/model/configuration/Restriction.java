package io.moquette.fce.model.configuration;
import io.moquette.fce.model.common.DataUnit;
import io.moquette.fce.model.common.IValid;
import io.moquette.fce.service.FceServiceFactory;
import io.moquette.plugin.AuthorizationProperties;

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
	private final DataSchema dataSchema;

	public Restriction(FceAction mqttAction, int messageCount, int maxMessageSizeKb, int totalMessageSizeKb, DataUnit sizeUnit,
			DataSchema dataSchema) {
		super();
		this.messageCount = messageCount;
		this.maxMessageSize = maxMessageSizeKb;
		this.totalMessageSize = totalMessageSizeKb;
		this.sizeUnit = sizeUnit;
		this.dataSchema = dataSchema;
		this.mqttAction = mqttAction;
	}

	public int getMessageCount() {
		return messageCount;
	}


	public DataUnit getDataUnit() {
		return sizeUnit;
	}

	public DataSchema getDataSchema() {
		return dataSchema;
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

	public boolean isValidCommon(FceServiceFactory services, AuthorizationProperties props){
		if(getMaxMessageSize() > 0 && isMessageSizeGreaterThanMaxMessageSize(props)){
				return false;
		}
		
		if (getDataSchema() != null) {
			return services.getSchemaValidationService(getDataSchema()).isSchemaValid(props.getMessage());
		}
		
		return true;
	}

	private boolean isMessageSizeGreaterThanMaxMessageSize(AuthorizationProperties props) {
		return props.getMessage().position() > (getMaxMessageSize() * getSizeUnit().getMultiplier());
	}
}