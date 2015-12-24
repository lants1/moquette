package io.moquette.fce.model.configuration;

/**
 * DataSchema model for validation.
 * 
 * @author lants1
 *
 */
public class DataSchema {

	private final SchemaType schemaType;
	private final String schemaTopic;
	
	public DataSchema(SchemaType schemaType, String schemaTopic) {
		super();
		this.schemaType = schemaType;
		this.schemaTopic = schemaTopic;
	}

	public SchemaType getSchemaType() {
		return schemaType;
	}
	
	public String getSchemaTopic() {
		return schemaTopic;
	}
	
}
