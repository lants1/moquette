package org.eclipse.moquette.fce.model.info;

import com.google.gson.annotations.SerializedName;

public enum InfoMessageType {
	@SerializedName("GLOBAL_QUOTA_DEPLETED")
	GLOBAL_QUOTA_DEPLETED("GLOBAL_QUOTA_DEPLETED"),
	@SerializedName("PRIVATE_QUOTA_DEPLETED")
	PRIVATE_QUOTA_DEPLETED("GLOBAL_QUOTA_DEPLETED"),
	@SerializedName("MISSING_ADMIN_RIGHTS")
	MISSING_ADMIN_RIGHTS("MISSING_ADMIN_RIGHTS"),
	@SerializedName("COMMON")
	AUTHORIZATION_EXCEPTION("COMMON");
	
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
