package org.eclipse.moquette.fce.service.impl.validation;

import java.nio.ByteBuffer;

public interface ISchemaValidationService {
	
	boolean isSchemaValid(ByteBuffer schemaDefinition, ByteBuffer msgToValidate);
	
}
