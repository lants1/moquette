package io.moquette.fce.service.validation;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

import io.moquette.fce.common.io.ByteBufferInputStream;

/**
 * Utility class for xml schema validation.
 * 
 * @author lants1
 *
 */
public final class XmlSchemaValidationService implements ISchemaValidationService {

	private static final Logger LOGGER = Logger.getLogger(XmlSchemaValidationService.class.getName());
	
	private ByteBuffer schema;
	
	public XmlSchemaValidationService(ByteBuffer schema){
		this.schema = schema;
	}
	
	@Override
	public boolean isSchemaValid(ByteBuffer xmlToValidateBfr) {
		try {
			Source xmlToValidate = new StreamSource(new ByteBufferInputStream(xmlToValidateBfr));
			Source schemaSource = new StreamSource(new ByteBufferInputStream(schema));
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schemaToValidate = schemaFactory.newSchema(schemaSource);
			Validator validator = schemaToValidate.newValidator();
			validator.validate(xmlToValidate);
		} catch (SAXException | IOException e) {
			LOGGER.log(Level.INFO, "xml schema validation failed", e);
			return false;
		}

		return true;
	}
}
