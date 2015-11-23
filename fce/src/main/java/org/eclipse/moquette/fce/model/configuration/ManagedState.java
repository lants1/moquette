package org.eclipse.moquette.fce.model.configuration;

import com.google.gson.annotations.SerializedName;

public enum ManagedState {
	@SerializedName("ACTIVE") ACTIVE,
	@SerializedName("INACTIVE") INACTIVE
}
