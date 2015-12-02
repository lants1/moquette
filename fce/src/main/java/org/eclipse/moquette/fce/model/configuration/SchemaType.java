package org.eclipse.moquette.fce.model.configuration;

import com.google.gson.annotations.SerializedName;

/**
 * Defines valid SchemaTypes for Schema validation.
 * 
 * @author lants1
 *
 */
public enum SchemaType {
	@SerializedName("WSDL")
	WSDL(), 
	@SerializedName("JSON_SCHEMA")
	JSON_SCHEMA()

}
