package org.eclipse.moquette.fce.event;

import org.apache.commons.codec.binary.StringUtils;
import org.eclipse.moquette.fce.service.IFceServiceFactory;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.MqttAction;

public abstract class FceEventHandler {

	final IFceServiceFactory services;
	final String pluginClientIdentifer;

	public FceEventHandler(IFceServiceFactory services, String pluginClientIdentifier) {
		this.services = services;
		this.pluginClientIdentifer = pluginClientIdentifier;
	}

	protected boolean isPluginClient(AuthorizationProperties properties) {
		if (!pluginClientIdentifer.isEmpty()) {
			return StringUtils.equals(properties.getClientId(), pluginClientIdentifer);
		}
		return false;
	}

	public IFceServiceFactory getServices() {
		return services;
	}

	public String getPluginClientIdentifer() {
		return pluginClientIdentifer;
	}

	public abstract boolean canDoOperation(AuthorizationProperties properties, MqttAction operation);
}
