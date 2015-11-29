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
import org.eclipse.moquette.fce.model.info.InfoMessageType;
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
	public boolean canDoOperation(AuthorizationProperties props, MqttAction action) {
		log.info("recieved Intent-Event on:" + props.getTopic() + "from client:" + props.getClientId() + " and action:"
				+ action);

		Boolean preCheckState = preCheckManagedZone(props, action);
		if (preCheckState != null) {
			return preCheckState;
		}

		try {
			ManagedTopic topic = new ManagedTopic(ManagedZoneUtil.removeZoneIdentifier(props.getTopic()));
			UserConfiguration newConfig = getServices().getJsonParser()
					.deserializeUserConfiguration(props.getMessage());

			if (getServices().getConfigDb(ManagedScope.GLOBAL).isManaged(topic)) {
				if (ManagedScope.PRIVATE.equals(newConfig.getManagedScope())) {
					log.info("accepted Event on:" + props.getTopic() + "from client:" + props.getClientId()
							+ " and action:" + action);
					return true;
				}

				UserConfiguration userConfig = getServices().getConfigDb(ManagedScope.GLOBAL).getConfiguration(props);
				if (userConfig == null) {
					return false;
				}
				if (AdminPermission.NONE.equals(userConfig.getAdminPermission())) {
					logAndSendInfoMsg(InfoMessageType.MISSING_ADMIN_RIGHTS, props, action);
					return false;
				}
				if (userConfig.isValidForEveryone()) {
					List<UserQuota> quotasToRemove = getUnneededGlobalQuotas(topic);

					for (UserQuota quotaToRemove : quotasToRemove) {
						deleteQuota(topic, quotaToRemove);
					}
				}
			} else {

				ManagedZone configurationZone;
				ManagedZone quotaZone;
				if (ManagedScope.GLOBAL.equals(newConfig.getManagedScope())) {
					configurationZone = ManagedZone.CONFIG_GLOBAL;
					quotaZone = ManagedZone.QUOTA_GLOBAL;
				} else if (ManagedScope.PRIVATE.equals(newConfig.getManagedScope())) {
					configurationZone = ManagedZone.CONFIG_PRIVATE;
					quotaZone = ManagedZone.QUOTA_PRIVATE;
				} else {
					return false;
				}

				getServices().getConfigDb(newConfig.getManagedScope())
						.put(topic.getIdentifier(newConfig, configurationZone), newConfig);
				getServices().getMqtt().publish(topic.getIdentifier(newConfig, configurationZone),
						getServices().getJsonParser().serialize(newConfig));

				storeNewQuotaForUserConfiguration(topic, newConfig, quotaZone);
			}
			log.info("accepted Intent-Event on:" + props.getTopic() + "from client:" + props.getClientId()
					+ " and action:" + action);

			return true;

		} catch (FceAuthorizationException e) {
			log.warning(e.toString());
			return false;
		}
	}

	private List<UserQuota> getUnneededGlobalQuotas(ManagedTopic topic) {
		List<UserQuota> quotasToRemove = getServices().getQuotaDb(ManagedScope.GLOBAL).getAllForTopic(topic);
		List<UserConfiguration> configs = getServices().getConfigDb(ManagedScope.GLOBAL).getAllForTopic(topic);
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

		getServices().getQuotaDb(ManagedScope.GLOBAL).put(userTopicIdentifier, quotaToRemove);
		getServices().getMqtt().delete(userTopicIdentifier);
	}

}
