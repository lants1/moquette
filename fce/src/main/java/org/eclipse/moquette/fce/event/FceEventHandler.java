package org.eclipse.moquette.fce.event;

import java.util.logging.Logger;

import org.apache.commons.codec.binary.StringUtils;
import org.eclipse.moquette.fce.service.IFceServiceFactory;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.MqttAction;

public abstract class FceEventHandler {

	private final static Logger log = Logger.getLogger(ManagedIntentHandler.class.getName());

	final IFceServiceFactory services;
	final String pluginClientIdentifer;

	public FceEventHandler(IFceServiceFactory services, String pluginClientIdentifier) {
		this.services = services;
		this.pluginClientIdentifer = pluginClientIdentifier;
	}

	public IFceServiceFactory getServices() {
		return services;
	}

	public String getPluginClientIdentifer() {
		return pluginClientIdentifer;
	}

	public Boolean preCheckManagedZone(AuthorizationProperties properties, MqttAction action) {
		if (properties.getAnonymous()) {
			return Boolean.FALSE;
		}

		if (isPluginClient(properties)) {
			log.fine("can do operation:" + action + " for topic:" + properties.getTopic()
					+ " because it's plugin client: " + properties.getUser());
			return Boolean.TRUE;
		}
		if (!services.getAuthorization().getBasicPermission(properties.getTopic()).isAllowed(action)) {
			return Boolean.FALSE;
		}
		return null;
	}
	
	private boolean isPluginClient(AuthorizationProperties properties) {
		if (!pluginClientIdentifer.isEmpty()) {
			return StringUtils.equals(properties.getClientId(), getPluginClientIdentifer());
		}
		return false;
	}

	public abstract boolean canDoOperation(AuthorizationProperties properties, MqttAction operation);
}
