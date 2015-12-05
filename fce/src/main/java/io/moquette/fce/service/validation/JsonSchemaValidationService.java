package io.moquette.fce.service.validation;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

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

	private static final Logger LOGGER = Logger.getLogger(JsonSchemaValidationService.class.getName());

	private ByteBuffer schema;

	public JsonSchemaValidationService(ByteBuffer schema) {
		this.schema = schema;
	}

	@Override
	public boolean isSchemaValid(ByteBuffer msgToValidate) {
		try {
			final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();

			BufferedReader schemaReader = new BufferedReader(
					new InputStreamReader(new ByteArrayInputStream(schema.array()), StandardCharsets.UTF_8));
			BufferedReader msgReader = new BufferedReader(
					new InputStreamReader(new ByteArrayInputStream(msgToValidate.array()), StandardCharsets.UTF_8));

			JsonNode schemaJson = JsonLoader.fromReader(schemaReader);
			JsonNode jsonToVavlidate = JsonLoader.fromReader(msgReader);
			factory.getJsonSchema(schemaJson);
			JsonSchema validationSchema = factory.getJsonSchema(schemaJson);

			return validationSchema.validate(jsonToVavlidate).isSuccess();
		} catch (ProcessingException | IOException e) {
			LOGGER.log(Level.INFO, "json schema validation failed: ", e);
			return false;
		}
	}
}
