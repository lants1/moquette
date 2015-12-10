package io.moquette.fce.service.validation;

import java.nio.ByteBuffer;

/**
 * 
 * 
 * @author lants1
 *
 */
public interface ISchemaValidationService {
	
	/**
	 * Validates a message according to the initizalized schema of the Service.
	 * 
	 * @param msgToValidate
	 * @return true if schema valid
	 */
	boolean isSchemaValid(ByteBuffer msgToValidate);
	
}
