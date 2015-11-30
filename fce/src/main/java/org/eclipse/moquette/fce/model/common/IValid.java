package org.eclipse.moquette.fce.model.common;

import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.MqttAction;

/**
 * Marks model classes which are validatable and let them implement the defined validation method.
 * Validation is a cross cutting concern and is in the fce plugin implemented on the model.
 * 
 * @author lants1 
 *
 */
public interface IValid {

	/**
	 * Validates a Requests.
	 * 
	 * @param AuthorizationProperties props
	 * @param MqttAction operation
	 * @return boolean isValid
	 */
	boolean isValid(AuthorizationProperties props, MqttAction operation);
}
