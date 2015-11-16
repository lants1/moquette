package org.eclipse.moquette.fce.model.configuration;

import com.google.gson.annotations.SerializedName;

public enum ManagedState {
	@SerializedName("MANAGED") MANAGED,
	@SerializedName("UNMANAGED") UNMANAGED
}
