package org.eclipse.moquette.fce.service.validation;

import java.nio.ByteBuffer;

public interface ISchemaValidationService {
	
	boolean isSchemaValid(ByteBuffer msgToValidate);
	
}
