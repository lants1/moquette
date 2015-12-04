package io.moquette.fce.model.configuration;

import com.google.gson.annotations.SerializedName;

/**
 * Enum which describes all available AdminPermissions.
 * 
 * @author lants1
 *
 */
public enum AdminPermission {
	@SerializedName("NONE")
	NONE,
	@SerializedName("ALL")
	ALL;
    
}