package org.eclipse.moquette.fce.event;

import org.eclipse.moquette.fce.service.FceServiceFactory;

public class PluginEventHandler {

	FceServiceFactory services;

	public PluginEventHandler(FceServiceFactory services) {
		this.services = services;
	}

	public boolean checkValid(String username, byte[] password) {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean canWrite(String topic, String user, String client) {
		return services.getAuthorizationService().getBasicPermission(topic).isWriteable();
	}

	public boolean canRead(String topic, String user, String client) {
		return services.getAuthorizationService().getBasicPermission(topic).isReadable();
	}
}
