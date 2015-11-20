package org.eclipse.moquette.fce.event;

import java.util.logging.Logger;

import org.apache.commons.codec.binary.StringUtils;
import org.eclipse.moquette.fce.common.FceHashUtil;
import org.eclipse.moquette.fce.exception.FceNoAuthorizationPossibleException;
import org.eclipse.moquette.fce.model.ManagedTopic;
import org.eclipse.moquette.fce.model.configuration.UserConfiguration;
import org.eclipse.moquette.fce.model.quota.Quota;
import org.eclipse.moquette.fce.service.FceServiceFactory;
import org.eclipse.moquette.plugin.AuthenticationProperties;
import org.eclipse.moquette.plugin.AuthorizationProperties;

public class PluginEventHandler {

	private final static Logger log = Logger.getLogger(PluginEventHandler.class.getName());

	FceServiceFactory services;
	String pluginClientIdentifer;

	public PluginEventHandler(FceServiceFactory services, String pluginClientIdentifier) {
		this.services = services;
		this.pluginClientIdentifer = pluginClientIdentifier;
	}

	public boolean checkValid(AuthenticationProperties props) {
		log.fine("recieved checkValid Event for " + props.getUsername());

		return FceHashUtil.validateClientIdHash(props);
	}

	// TODO lants1 evil, evil.. refactor this methods...
	public boolean canWrite(AuthorizationProperties properties) {
		log.fine("recieved canWrite Event on " + properties.getTopic() + "from client" + properties.getClientId());
		if (isPluginClient(properties)) {
			return true;
		}

		if (!services.getAuthorizationService().getBasicPermission(properties.getTopic()).isWriteable()) {
			return false;
		}

		if (!services.getConfigDbService().isTopicFilterManaged(new ManagedTopic(properties.getTopic()))) {
			return true;
		}

		if(properties.getAnonymous()){
			return false;
		}
		
		UserConfiguration userConfig;
		Quota quota;

		try {
			userConfig = services.getConfigDbService().getConfiguration(properties);
			quota = services.getQuotaDbService().getQuota(properties);
			return true;
		} catch (FceNoAuthorizationPossibleException e) {
			log.warning(e.toString());
			return false;
		}
	}

	public boolean canRead(AuthorizationProperties properties) {
		log.fine("recieved canRead Event on " + properties.getTopic() + "from client" + properties.getClientId());
		if (isPluginClient(properties)) {
			return true;
		}

		if (!services.getAuthorizationService().getBasicPermission(properties.getTopic()).isReadable()) {
			return false;
		}

		if (!services.getConfigDbService().isTopicFilterManaged(new ManagedTopic(properties.getTopic()))) {
			return true;
		}

		if(properties.getAnonymous()){
			return false;
		}
		
		UserConfiguration userConfig;
		Quota quota;

		try {
			userConfig = services.getConfigDbService().getConfiguration(properties);
			quota = services.getQuotaDbService().getQuota(properties);
			return true;
		} catch (FceNoAuthorizationPossibleException e) {
			log.warning(e.toString());
			// TODO lants1 publish info message services.getMqttService().publish(topic, json, retained);
			return false;
		}
	}

	private boolean isPluginClient(AuthorizationProperties properties) {
		if (!pluginClientIdentifer.isEmpty()) {
			return StringUtils.equals(properties.getClientId(), pluginClientIdentifer);
		}
		return false;
	}
}
