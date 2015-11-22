package org.eclipse.moquette.fce.event;

import java.util.logging.Logger;

import org.eclipse.moquette.fce.common.ManagedZone;
import org.eclipse.moquette.fce.common.ManagedZoneUtil;
import org.eclipse.moquette.fce.exception.FceAuthorizationException;
import org.eclipse.moquette.fce.model.ManagedTopic;
import org.eclipse.moquette.fce.model.configuration.AdminActionPermission;
import org.eclipse.moquette.fce.model.configuration.AdminGroupPermission;
import org.eclipse.moquette.fce.model.configuration.UserConfiguration;
import org.eclipse.moquette.fce.service.IFceServiceFactory;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.MqttAction;

public class ManagedStoreHandler extends FceEventHandler {

	private final static Logger log = Logger.getLogger(ManagedStoreHandler.class.getName());

	public ManagedStoreHandler(IFceServiceFactory services, String pluginClientIdentifier) {
		super(services, pluginClientIdentifier);
	}

	// TODO lants1 to complicated....
	public boolean canDoOperation(AuthorizationProperties properties, MqttAction action) {
		if (isPluginClient(properties)) {
			return Boolean.TRUE;
		}
		if (!services.getAuthorization().getBasicPermission(properties.getTopic()).isAllowed(action)) {
			return Boolean.FALSE;
		}

		if (properties.getAnonymous()) {
			return Boolean.FALSE;
		}

		try {
			ManagedZone zone = ManagedZoneUtil.getZoneForTopic(properties.getTopic());
			ManagedTopic topic = new ManagedTopic(ManagedZoneUtil.removeZoneIdentifier(properties.getTopic()));
			if (ManagedZone.INTENT.equals(zone)) {
				if (services.getConfigDb().isManaged(topic)) {
					UserConfiguration userConfig = services.getConfigDb().getConfiguration(properties);
					if (userConfig == null) {
						return false;
					}
					if (AdminGroupPermission.NONE.equals(userConfig.getAdminGroupPermission())) {
						return false;
					}

					// TODO lants1 better idea for handling manage permission...
					UserConfiguration parsedConfig = services.getJsonParser()
							.deserializeUserConfiguration(new String(properties.getMessage().array(),
									properties.getMessage().position(), properties.getMessage().limit()));

					if (AdminGroupPermission.SELF.equals(userConfig.getAdminGroupPermission())) {
						if (!parsedConfig.getUserIdentifier().equals(properties.getClientId())) {
							return false;
						}
					}

					if (AdminActionPermission.PUBLISH.equals(userConfig.getAdminActionPermission())) {
						if (!parsedConfig.getSubscribeRestrictions().isEmpty()) {
							return false;
						}
					}

					if (AdminActionPermission.SUBSCRIBE.equals(userConfig.getAdminActionPermission())) {
						if (!parsedConfig.getPublishRestrictions().isEmpty()) {
							return false;
						}
					}
				}
				// TODO lants1 no need to set something in db store or mqtt store;)
				return true;
			} else {
				return topic.isAllowedForUser(properties);
			}

		} catch (FceAuthorizationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

}
