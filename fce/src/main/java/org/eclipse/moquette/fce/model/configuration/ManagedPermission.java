package org.eclipse.moquette.fce.model.configuration;

import org.eclipse.moquette.plugin.MqttOperation;

import com.google.gson.annotations.SerializedName;

/**
 * Defines Manage possibility's.
 * 
 * @author lants1
 *
 */
public enum ManagedPermission {
	@SerializedName("PUBLISH")
	PUBLISH("PUBLISH", false, true), 
	@SerializedName("SUBSCRIBE")
	SUBSCRIBE("SUBSCRIBE", true, false),
	@SerializedName("ALL")
	ALL("ALL", true, true);
	
	private String value;
	private boolean canRead;
	private boolean canWrite;

    private ManagedPermission(String value, boolean canRead, boolean canWrite)
    {
        this.value=value;
        this.canRead=canRead;
        this.canWrite=canWrite;
    }

    public String getValue()
    {
        return(value);
    }
    
    public boolean canDoOperation(MqttOperation operation){
    	if(MqttOperation.PUBLISH == operation){
    		return canWrite;
    	}
    	if(MqttOperation.SUBSCRIBE == operation){
    		return canRead;
    	}
    	return false;
    }
    
}
