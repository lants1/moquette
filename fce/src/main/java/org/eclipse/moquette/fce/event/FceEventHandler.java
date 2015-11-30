package org.eclipse.moquette.fce.event;

import java.util.logging.Logger;

import org.apache.commons.codec.binary.StringUtils;
import org.eclipse.moquette.fce.common.converter.QuotaConverter;
import org.eclipse.moquette.fce.exception.FceAuthorizationException;
import org.eclipse.moquette.fce.model.common.ManagedTopic;
import org.eclipse.moquette.fce.model.common.ManagedZone;
import org.eclipse.moquette.fce.model.common.TopicPermission;
import org.eclipse.moquette.fce.model.configuration.UserConfiguration;
import org.eclipse.moquette.fce.model.info.InfoMessage;
import org.eclipse.moquette.fce.model.info.InfoMessageType;
import org.eclipse.moquette.fce.model.quota.UserQuota;
import org.eclipse.moquette.fce.service.IFceServiceFactory;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.MqttAction;

/**
 * Abstract handler for each Managed Event with common methods... 
 * 
 * @author lants1
 *
 */
public abstract class FceEventHandler {

	private final static Logger log = Logger.getLogger(FceEventHandler.class.getName());

	private final IFceServiceFactory services;
	private final String pluginClientIdentifer;

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
			log.info("anonymous login, authorization rejected");
			return Boolean.FALSE;
		}

		if (isPluginClient(properties)) {
			log.info("can do operation:" + action + " for topic:" + properties.getTopic()
					+ " because it's plugin client: " + properties.getUser());
			return Boolean.TRUE;
		}
		if (!TopicPermission.getBasicPermission(properties.getTopic()).isAllowed(action)) {
			log.info("no permission to "+action + " topic"+properties.getTopic());
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

	protected void logAndSendInfoMsg(InfoMessageType msgType, AuthorizationProperties props, MqttAction action) {
		log.info(msgType + " for topic:" + props.getTopic() + " user: " + props.getUser() + " action:" + action);
		InfoMessage infoMsg = new InfoMessage(props.getUser(), props.getClientId(), msgType, "mqttaction: " + action);
		getServices().getMqtt().publish(new ManagedTopic(props.getTopic()).getIdentifier(props, ManagedZone.INFO),
				getServices().getJsonParser().serialize(infoMsg));
	}
	
	protected void storeNewQuotaForUserConfiguration(ManagedTopic topic, UserConfiguration usrConfig, ManagedZone quotaZone)
			throws FceAuthorizationException {
		UserQuota subQuota = QuotaConverter.convertSubscribeConfiguration(usrConfig);
		UserQuota pubQuota = QuotaConverter.convertPublishConfiguration(usrConfig);

		String subQuotaTopic = topic.getIdentifier(subQuota, quotaZone, MqttAction.SUBSCRIBE);
		String pubQuotaTopic = topic.getIdentifier(pubQuota, quotaZone, MqttAction.PUBLISH);

		getServices().getQuotaDb(usrConfig.getManagedScope()).put(subQuotaTopic, subQuota, true);
		getServices().getQuotaDb(usrConfig.getManagedScope()).put(pubQuotaTopic, pubQuota, true);
		getServices().getMqtt().publish(subQuotaTopic, getServices().getJsonParser().serialize(subQuota));
		getServices().getMqtt().publish(pubQuotaTopic, getServices().getJsonParser().serialize(pubQuota));
	}

	public abstract boolean canDoOperation(AuthorizationProperties properties, MqttAction operation);
}
