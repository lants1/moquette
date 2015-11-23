package org.eclipse.moquette.fce.model;

import com.google.gson.annotations.SerializedName;

public enum ManagedScope {
	@SerializedName("MANAGED") GLOBAL,
	@SerializedName("UNMANAGED") PRIVATE
}
