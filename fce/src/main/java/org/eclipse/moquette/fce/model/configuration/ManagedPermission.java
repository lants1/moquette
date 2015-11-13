package org.eclipse.moquette.fce.model.configuration;

import com.google.gson.annotations.SerializedName;

/**
 * Defines Manage possibility's.
 * 
 * @author lants1
 *
 */
public enum ManagedPermission {
	@SerializedName("NONE")
	NONE("NONE"), 
	@SerializedName("MYSELF")
	MYSELF("MYSELF"),
	@SerializedName("EVERYONE")
	EVERYONE("EVERYONE");
	
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
