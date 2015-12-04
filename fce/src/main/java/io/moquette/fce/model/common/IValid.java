package io.moquette.fce.model.common;

import io.moquette.fce.service.FceServiceFactory;
import io.moquette.plugin.AuthorizationProperties;
import io.moquette.plugin.MqttAction;

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
	 * @param services TODO
	 * @param FceContext context
	 * @param AuthorizationProperties props
	 * @param MqttAction operation
	 * 
	 * @return boolean isValid
	 */
	boolean isValid(FceServiceFactory services, AuthorizationProperties props, MqttAction operation);
}
