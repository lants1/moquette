package org.eclipse.moquette.fce.model.configuration;

import com.google.gson.annotations.SerializedName;

/**
 * Enum which describes the state of a configuration.
 * 
 * @author lants1
 *
 */
public enum ManagedState {
	@SerializedName("ACTIVE") ACTIVE,
	@SerializedName("INACTIVE") INACTIVE
}
