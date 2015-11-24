package org.eclipse.moquette.fce.event;

import java.util.List;
import java.util.logging.Logger;

import org.eclipse.moquette.fce.common.ManagedZone;
import org.eclipse.moquette.fce.common.ManagedZoneUtil;
import org.eclipse.moquette.fce.exception.FceAuthorizationException;
import org.eclipse.moquette.fce.model.ManagedScope;
import org.eclipse.moquette.fce.model.ManagedTopic;
import org.eclipse.moquette.fce.model.configuration.AdminPermission;
import org.eclipse.moquette.fce.model.configuration.UserConfiguration;
import org.eclipse.moquette.fce.model.quota.UserQuota;
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
		Boolean preCheckState = preCheckManagedZone(properties, action);
		if (preCheckState != null) {
			return preCheckState;
		}

		try {
			ManagedTopic topic = new ManagedTopic(ManagedZoneUtil.removeZoneIdentifier(properties.getTopic()));
			UserConfiguration newConfig = services.getJsonParser()
					.deserializeUserConfiguration(properties.getMessage());

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
				if (userConfig.isValidForEveryone()) {
					List<UserQuota> quotasToRemove = getUnneededGlobalQuotas(topic);

					for (UserQuota quotaToRemove : quotasToRemove) {
						deleteQuota(topic, quotaToRemove);
					}
				}
			}
			return true;

		} catch (FceAuthorizationException e) {
			log.warning(e.toString());
			return false;
		}
	}

	private List<UserQuota> getUnneededGlobalQuotas(ManagedTopic topic) {
		List<UserQuota> quotasToRemove = services.getQuotaDb(ManagedScope.GLOBAL).getAllForTopic(topic);
		List<UserConfiguration> configs = services.getConfigDb(ManagedScope.GLOBAL).getAllForTopic(topic);
		for (UserQuota quota : quotasToRemove) {
			String quotaIdentifier = quota.getUserIdentifier();
			for (UserConfiguration config : configs) {
				if (config.getUserIdentifier().equals(quotaIdentifier)) {
					quotasToRemove.remove(quota);
				}
			}

		}
		return quotasToRemove;
	}

	private void deleteQuota(ManagedTopic topic, UserQuota quotaToRemove) throws FceAuthorizationException {
		String userTopicIdentifier = topic.getIdentifier(quotaToRemove.getUserIdentifier(), ManagedZone.QUOTA_GLOBAL,
				quotaToRemove.getAction());

		services.getQuotaDb(ManagedScope.GLOBAL).put(userTopicIdentifier, quotaToRemove);
		services.getMqtt().delete(userTopicIdentifier);
	}

}
