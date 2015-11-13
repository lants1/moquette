package org.eclipse.moquette.fce.model.info;

import com.google.gson.annotations.SerializedName;

public enum InfoMessageType {
	@SerializedName("QUOTA_DEPLETED")
	QUOTA_DEPLETED("QUOTA_DEPLETED"),
	@SerializedName("DAILY")
	COMMON("COMMON");
	
	private String value;

    private InfoMessageType(String value)
    {
        this.value=value;
    }

    public String getValue()
    {
        return(value);
    }
}
