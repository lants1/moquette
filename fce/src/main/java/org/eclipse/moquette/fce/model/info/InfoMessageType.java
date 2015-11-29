package org.eclipse.moquette.fce.model.info;

import com.google.gson.annotations.SerializedName;

public enum InfoMessageType {
	@SerializedName("GLOBAL_QUOTA_DEPLETED")
	GLOBAL_QUOTA_DEPLETED("GLOBAL_QUOTA_DEPLETED"),
	
	@SerializedName("PRIVATE_QUOTA_DEPLETED")
	PRIVATE_QUOTA_DEPLETED("PRIVATE_QUOTA_DEPLETED"),
	
	@SerializedName("REJECECTED_ACCORDING_TO_GLOBAL_RESTRICTIONS")
	GLOBAL_CONFIG_REJECTED("REJECECTED_ACCORDING_TO_GLOBAL_RESTRICTIONS"),
	
	@SerializedName("REJECECTED_ACCORDING_TO_PRIVATE_RESTRICTIONS")
	PRIVATE_CONFIG_REJECTED("REJECECTED_ACCORDING_TO_PRIVATE_RESTRICTIONS"),
	
	@SerializedName("MISSING_ADMIN_RIGHTS")
	MISSING_ADMIN_RIGHTS("MISSING_ADMIN_RIGHTS"),
	
	@SerializedName("AUTHORIZATION_EXCEPTION")
	AUTHORIZATION_EXCEPTION("AUTHORIZATION_EXCEPTION");
	
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
