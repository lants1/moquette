package io.moquette.fce.model.configuration;

import com.google.gson.annotations.SerializedName;

import io.moquette.plugin.MqttAction;

/**
 * Defines Manage possibility's.
 * 
 * @author lants1
 *
 */
public enum FceAction {
	@SerializedName("PUBLISH")
	PUBLISH("PUBLISH", false, true), 
	@SerializedName("SUBSCRIBE")
	SUBSCRIBE("SUBSCRIBE", true, false),
	@SerializedName("ALL")
	ALL("ALL", true, true);
	
	private String value;
	private boolean canRead;
	private boolean canWrite;

    private FceAction(String value, boolean canRead, boolean canWrite)
    {
        this.value=value;
        this.canRead=canRead;
        this.canWrite=canWrite;
    }

    public String getValue()
    {
        return value;
    }
    
    public boolean canDoOperation(MqttAction operation){
    	if(MqttAction.PUBLISH == operation){
    		return canWrite;
    	}
    	if(MqttAction.SUBSCRIBE == operation){
    		return canRead;
    	}
    	return false;
    }
    
}
