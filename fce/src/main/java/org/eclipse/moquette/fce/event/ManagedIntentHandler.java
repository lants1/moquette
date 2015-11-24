package org.eclipse.moquette.fce.event;

import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.moquette.fce.common.ManagedZoneUtil;
import org.eclipse.moquette.fce.exception.FceAuthorizationException;
import org.eclipse.moquette.fce.model.ManagedScope;
import org.eclipse.moquette.fce.model.ManagedTopic;
import org.eclipse.moquette.fce.model.configuration.AdminPermission;
import org.eclipse.moquette.fce.model.configuration.UserConfiguration;
import org.eclipse.moquette.fce.service.IFceServiceFactory;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.MqttAction;

public class ManagedIntentHandler extends FceEventHandler {

	private final static Logger log = Logger.getLogger(ManagedIntentHandler.class.getName());

	public ManagedIntentHandler(IFceServiceFactory services, String pluginClientIdentifier) {
		super(services, pluginClientIdentifier);
	}

	// TODO lants1 to complicated....
	public boolean canDoOperation(AuthorizationProperties properties, MqttAction action) {
		if (isPluginClient(properties)) {
			log.fine("can do operation:" + action + " for topic:" + properties.getTopic()
			+ " because it's plugin client: " + properties.getUser());
			return Boolean.TRUE;
		}
		if (!services.getAuthorization().getBasicPermission(properties.getTopic()).isAllowed(action)) {
			return Boolean.FALSE;
		}

		if (properties.getAnonymous()) {
			return Boolean.FALSE;
		}

		try {
			ManagedTopic topic = new ManagedTopic(ManagedZoneUtil.removeZoneIdentifier(properties.getTopic()));
			UserConfiguration newConfig = services.getJsonParser()
					.deserializeUserConfiguration(new String(properties.getMessage().array(),
							properties.getMessage().position(), properties.getMessage().limit()));

			if (services.getConfigDb(ManagedScope.GLOBAL).isManaged(topic)) {
				if (ManagedScope.PRIVATE.equals(newConfig.getManagedScope())) {
					return true;
				}

				UserConfiguration userConfig = services.getConfigDb(ManagedScope.GLOBAL).getConfiguration(properties);
				if (userConfig == null) {
					return false;
				}
				if (AdminPermission.NONE.equals(userConfig.getAdminPermission())) {
					return false;
				}
				if (StringUtils.isEmpty(userConfig.getUserIdentifier())) {
					// TODO lants1 getAll Quota_GLOBAL - CONFIG_USERS and delete
					// them....
				}
			}
			return true;

		} catch (FceAuthorizationException e) {
			log.warning(e.toString());
			return false;
		}
	}

}
