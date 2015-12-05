package io.moquette.fce.service.validation;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import org.junit.Test;

import io.moquette.fce.common.ReadFileUtil;

public class XmlSchemaValidationServiceTest {

	@Test
	public void testValid() throws IOException, URISyntaxException {
		ByteBuffer schema = ByteBuffer.wrap(ReadFileUtil.readFileBytes("/validation/xml/sampleSchema.xml"));
		ByteBuffer valid = ByteBuffer.wrap(ReadFileUtil.readFileBytes("/validation/xml/valid.xml"));
		XmlSchemaValidationService validationSchema = new XmlSchemaValidationService(schema);
		assertTrue(validationSchema.isSchemaValid(valid));
	}
	
	@Test
	public void testInvalid() throws IOException, URISyntaxException {
		ByteBuffer schema = ByteBuffer.wrap(ReadFileUtil.readFileBytes("/validation/xml/sampleSchema.xml"));
		ByteBuffer invalid = ByteBuffer.wrap(ReadFileUtil.readFileBytes("/validation/xml/invalid.xml"));
		XmlSchemaValidationService validationSchema = new XmlSchemaValidationService(schema);
		assertFalse(validationSchema.isSchemaValid(invalid));
	}
	
	@Test
	public void testWrongScheam() throws IOException, URISyntaxException {
		ByteBuffer schema = ByteBuffer.wrap("asfdasdf".getBytes());
		ByteBuffer invalid = ByteBuffer.wrap(ReadFileUtil.readFileBytes("/validation/xml/invalid.xml"));
		XmlSchemaValidationService validationSchema = new XmlSchemaValidationService(schema);
		assertFalse(validationSchema.isSchemaValid(invalid));
	}

}
