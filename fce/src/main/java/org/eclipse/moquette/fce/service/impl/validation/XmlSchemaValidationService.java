package org.eclipse.moquette.fce.service.impl.validation;

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

import org.eclipse.moquette.fce.common.io.ByteBufferInputStream;
import org.xml.sax.SAXException;

/**
 * Utility class for xml schema validation.
 * 
 * @author lants1
 *
 */
public final class XmlSchemaValidationService implements ISchemaValidationService {

	private final static Logger log = Logger.getLogger(XmlSchemaValidationService.class.getName());
	
	@Override
	public boolean isSchemaValid(ByteBuffer schemaDefinitionBfr, ByteBuffer xmlToValidateBfr) {
		try {
			Source xmlToValidate = new StreamSource(new ByteBufferInputStream(xmlToValidateBfr));
			Source schemaToValidate = new StreamSource(new ByteBufferInputStream(schemaDefinitionBfr));
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory.newSchema(schemaToValidate);
			Validator validator = schema.newValidator();
			validator.validate(xmlToValidate);
		} catch (SAXException | IOException e) {
			log.log(Level.INFO, "xml schema validation failed", e);
			return false;
		}

		return true;
	}
}
