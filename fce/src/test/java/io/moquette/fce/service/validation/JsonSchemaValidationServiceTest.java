package io.moquette.fce.service.validation;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import org.junit.Test;

import io.moquette.fce.common.ReadFileUtil;

public class JsonSchemaValidationServiceTest {

	@Test
	public void testValid() throws IOException, URISyntaxException {
		ByteBuffer schema = ByteBuffer.wrap(ReadFileUtil.readFileBytes("/validation/json/fstab.json"));
		ByteBuffer jsonGood = ByteBuffer.wrap(ReadFileUtil.readFileBytes("/validation/json/fstab-good.json"));

		JsonSchemaValidationService validator = new JsonSchemaValidationService(schema);
		assertTrue(validator.isSchemaValid(jsonGood));
	}

	@Test
	public void testInvalid() throws IOException, URISyntaxException {
		ByteBuffer schema = ByteBuffer.wrap(ReadFileUtil.readFileBytes("/validation/json/fstab.json"));
		ByteBuffer badJson = ByteBuffer.wrap(ReadFileUtil.readFileBytes("/validation/json/fstab-bad.json"));

		JsonSchemaValidationService validator = new JsonSchemaValidationService(schema);
		assertFalse(validator.isSchemaValid(badJson));
	}

	@Test
	public void testBadSchema() throws IOException, URISyntaxException {
		ByteBuffer schema = ByteBuffer.wrap("asfdasfdsadf".getBytes());
		ByteBuffer badJson = ByteBuffer.wrap(ReadFileUtil.readFileBytes("/validation/json/fstab-bad.json"));

		JsonSchemaValidationService validator = new JsonSchemaValidationService(schema);
		assertFalse(validator.isSchemaValid(badJson));
	}
}
