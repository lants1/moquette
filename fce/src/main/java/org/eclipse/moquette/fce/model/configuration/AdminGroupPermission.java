package org.eclipse.moquette.fce.model.configuration;

import com.google.gson.annotations.SerializedName;

public enum AdminGroupPermission {
	@SerializedName("NONE")
	NONE, 
	@SerializedName("SUBSCRIBE")
	SELF,
	@SerializedName("ALL")
	ALL;
    
}