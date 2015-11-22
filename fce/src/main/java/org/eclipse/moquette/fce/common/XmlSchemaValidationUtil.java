package org.eclipse.moquette.fce.common;

import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.eclipse.moquette.fce.common.io.ByteBufferInputStream;
import org.xml.sax.SAXException;

public final class XmlSchemaValidationUtil {

	public static boolean isValidXmlFileAccordingToSchema(ByteBuffer fileMsg, String schemaUrl) {
		try {
			URL schemaFile = new URL(schemaUrl);
			Source xmlFile = new StreamSource(new ByteBufferInputStream(fileMsg));
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory.newSchema(schemaFile);
			Validator validator = schema.newValidator();
			validator.validate(xmlFile);
		} catch (SAXException | IOException e) {
			return false;
		}

		return true;
	}
}
