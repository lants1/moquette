package org.eclipse.moquette.fce.model.common;

import com.google.gson.annotations.SerializedName;

/**
 * Define all type of ManagedCycles.
 * 
 * @author lants1
 *
 */
public enum ManagedCycle {
	@SerializedName("HOURLY")
	HOURLY("HOURLY"),
	@SerializedName("DAILY")
	DAILY("DAILY"),
	@SerializedName("WEEKLY")
	WEEKLY("WEEKLY"),
	@SerializedName("MONTHLY")
	MONTHLY("MONTHLY"), 
	@SerializedName("YEARLY")
	YEARLY("YEARLY");
	
	private String value;

    private ManagedCycle(String value)
    {
        this.value=value;
    }

    public String getValue()
    {
        return(value);
    }
}
