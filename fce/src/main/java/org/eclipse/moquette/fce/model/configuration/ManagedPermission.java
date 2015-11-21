package org.eclipse.moquette.fce.model.configuration;

import com.google.gson.annotations.SerializedName;

/**
 * Defines Manage possibility's.
 * 
 * @author lants1
 *
 */
public enum ManagedPermission {
	@SerializedName("PUBLISH")
	PUBLISH("PUBLISH"), 
	@SerializedName("SUBSCRIBE")
	SUBSCRIBE("SUBSCRIBE"),
	@SerializedName("ALL")
	ALL("ALL");
	
	private String value;

    private ManagedPermission(String value)
    {
        this.value=value;
    }

    public String getValue()
    {
        return(value);
    }
}
