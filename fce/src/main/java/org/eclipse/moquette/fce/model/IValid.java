package org.eclipse.moquette.fce.model;

import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.MqttAction;

public interface IValid {

	boolean isValid(AuthorizationProperties props, MqttAction operation);
}
