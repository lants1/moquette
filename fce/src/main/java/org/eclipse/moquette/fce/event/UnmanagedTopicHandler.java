package org.eclipse.moquette.fce.event;

import org.eclipse.moquette.fce.service.IFceServiceFactory;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.MqttAction;

public class UnmanagedTopicHandler extends FceEventHandler {

	public UnmanagedTopicHandler(IFceServiceFactory services, String pluginClientIdentifier) {
		super(services, pluginClientIdentifier);
	}

	@Override
	public boolean canDoOperation(AuthorizationProperties properties, MqttAction operation) {
		return true;
	}

}
