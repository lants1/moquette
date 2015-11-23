package org.eclipse.moquette.fce.model.configuration;

import com.google.gson.annotations.SerializedName;

public enum AdminPermission {
	@SerializedName("NONE")
	NONE,
	@SerializedName("ALL")
	ALL;
    
}