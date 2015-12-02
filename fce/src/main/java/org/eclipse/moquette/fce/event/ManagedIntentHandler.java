package org.eclipse.moquette.fce.event;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.moquette.fce.common.util.ManagedZoneUtil;
import org.eclipse.moquette.fce.context.FceContext;
import org.eclipse.moquette.fce.exception.FceAuthorizationException;
import org.eclipse.moquette.fce.model.common.ManagedScope;
import org.eclipse.moquette.fce.model.common.ManagedTopic;
import org.eclipse.moquette.fce.model.common.ManagedZone;
import org.eclipse.moquette.fce.model.configuration.AdminPermission;
import org.eclipse.moquette.fce.model.configuration.UserConfiguration;
import org.eclipse.moquette.fce.model.info.InfoMessageType;
import org.eclipse.moquette.fce.model.quota.UserQuota;
import org.eclipse.moquette.fce.service.FceServiceFactory;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.MqttAction;

/**
 * Handler which is used for each publish intent.
 * 
 * @author lants1
 *
 */
public class ManagedIntentHandler extends FceEventHandler {

	private final static Logger log = Logger.getLogger(ManagedIntentHandler.class.getName());

	public ManagedIntentHandler(FceContext context, FceServiceFactory services) {
		super(context, services);
	}

	public boolean canDoOperation(AuthorizationProperties props, MqttAction action) {
		String usernameHashFromRequest = getContext().getHashAssignment().get(props.getClientId());
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

			if (getContext().getConfigurationStore(ManagedScope.GLOBAL).isManaged(topic)) {
				if (ManagedScope.PRIVATE.equals(newConfig.getManagedScope())) {
					if (getContext().getHashAssignment().get(props.getClientId()).equals(newConfig.getUserHash())) {
						log.info("accepted Event on:" + props.getTopic() + "from client:" + props.getClientId()
								+ " and action:" + action);
						storeUserConfiguration(topic, newConfig);
						return true;
					}
					return false;
				}

				UserConfiguration userConfig = getContext().getConfigurationStore(ManagedScope.GLOBAL).getConfiguration(props.getTopic(), usernameHashFromRequest);
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
			}
			storeUserConfiguration(topic, newConfig);

			log.info("accepted Intent-Event on:" + props.getTopic() + "from client:" + props.getClientId()
					+ " and action:" + action);

			return true;

		} catch (FceAuthorizationException e) {
			log.log(Level.WARNING, "could not authorize request", e);
			return false;
		}
	}

	private void storeUserConfiguration(ManagedTopic topic, UserConfiguration newConfig)
			throws FceAuthorizationException {
		ManagedZone configurationZone;
		ManagedZone quotaZone;
		if (ManagedScope.GLOBAL.equals(newConfig.getManagedScope())) {
			configurationZone = ManagedZone.CONFIG_GLOBAL;
			quotaZone = ManagedZone.QUOTA_GLOBAL;
		} else if (ManagedScope.PRIVATE.equals(newConfig.getManagedScope())) {
			configurationZone = ManagedZone.CONFIG_PRIVATE;
			quotaZone = ManagedZone.QUOTA_PRIVATE;
		} else {
			throw new FceAuthorizationException("invalid managed scope");
		}

		getContext().getConfigurationStore(newConfig.getManagedScope()).put(topic.getIdentifier(newConfig, configurationZone),
				newConfig);
		getServices().getMqtt().publish(topic.getIdentifier(newConfig, configurationZone),
				getServices().getJsonParser().serialize(newConfig));

		storeNewQuotaForUserConfiguration(topic, newConfig, quotaZone);
	}

	private List<UserQuota> getUnneededGlobalQuotas(ManagedTopic topic) {
		List<UserQuota> quotasToRemove = getContext().getQuotaStore(ManagedScope.GLOBAL).getAllForTopic(topic);
		List<UserConfiguration> configs = getContext().getConfigurationStore(ManagedScope.GLOBAL).getAllForTopic(topic);
		for (UserQuota quota : quotasToRemove) {
			String quotaIdentifier = quota.getUserHash();
			for (UserConfiguration config : configs) {
				if (config.getUserHash().equals(quotaIdentifier)) {
					quotasToRemove.remove(quota);
				}
			}

		}
		return quotasToRemove;
	}

	private void deleteQuota(ManagedTopic topic, UserQuota quotaToRemove) throws FceAuthorizationException {
		String userTopicIdentifier = topic.getIdentifier(quotaToRemove.getUserHash(), ManagedZone.QUOTA_GLOBAL,
				quotaToRemove.getAction());

		getContext().getQuotaStore(ManagedScope.GLOBAL).put(userTopicIdentifier, quotaToRemove);
		getServices().getMqtt().delete(userTopicIdentifier);
	}

}
