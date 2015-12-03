package org.eclipse.moquette.fce.service.validation;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.stream.StreamSource;

import org.eclipse.moquette.fce.common.io.ByteBufferInputStream;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

/**
 * Utility class for json schema validation.
 * 
 * @author lants1
 *
 */
public final class JsonSchemaValidationService implements ISchemaValidationService {
	
	private final static Logger log = Logger.getLogger(JsonSchemaValidationService.class.getName());
	private ByteBuffer schema;
	
	public JsonSchemaValidationService(ByteBuffer schema){
		this.schema = schema;
	}
	
	@Override
	public boolean isSchemaValid(ByteBuffer msgToValidate) {
		try {
			final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
			final JsonNode schemaJson = JsonLoader
					.fromReader(new StreamSource(new ByteBufferInputStream(schema)).getReader());
			final JsonNode jsonToVavlidate = JsonLoader
					.fromReader(new StreamSource(new ByteBufferInputStream(msgToValidate)).getReader());
			factory.getJsonSchema(schemaJson);
			final JsonSchema schema = factory.getJsonSchema(schemaJson);

			return schema.validate(jsonToVavlidate).isSuccess();
		} catch (ProcessingException | IOException e) {
			log.log(Level.INFO, "json schema validation failed: ", e);
			return false;
		}
	}
}
