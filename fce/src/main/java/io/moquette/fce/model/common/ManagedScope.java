package io.moquette.fce.model.common;

import com.google.gson.annotations.SerializedName;

public enum ManagedScope {
	@SerializedName("GLOBAL") GLOBAL,
	@SerializedName("PRIVATE") PRIVATE
}
