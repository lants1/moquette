package io.moquette.fce.model.common;

import com.google.gson.annotations.SerializedName;

/**
 * ManagedScope enum used to distinct private and global configurations.
 * 
 * @author lants1
 *
 */
public enum ManagedScope {
	@SerializedName("GLOBAL") GLOBAL,
	@SerializedName("PRIVATE") PRIVATE
}
