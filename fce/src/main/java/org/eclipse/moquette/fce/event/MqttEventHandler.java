package org.eclipse.moquette.fce.event;

import java.util.logging.Logger;

import org.eclipse.moquette.fce.common.util.ManagedZoneUtil;
import org.eclipse.moquette.fce.exception.FceSystemException;
import org.eclipse.moquette.fce.model.common.ManagedTopic;
import org.eclipse.moquette.fce.model.common.ManagedZone;
import org.eclipse.moquette.fce.model.configuration.UserConfiguration;
import org.eclipse.moquette.fce.model.quota.UserQuota;
import org.eclipse.moquette.fce.service.IFceServiceFactory;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.MqttAction;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttEventHandler extends FceEventHandler implements MqttCallback {

	private final static Logger log = Logger.getLogger(MqttEventHandler.class.getName());

	public MqttEventHandler(IFceServiceFactory services, String pluginClientIdentifier) {
		super(services, pluginClientIdentifier);
	}

	@Override
	public void connectionLost(Throwable arg0) {
		try {
			log.warning("internal plugin mqttclient conection to broker connection lost");
			getServices().getMqtt().connect();
		} catch (MqttException e) {
			log.severe("internal plugin mqttclient could not reconnect to broker");
			throw new FceSystemException(e);
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// doNothing
	}

	@Override
	public void messageArrived(String topicIdentifier, MqttMessage message) throws Exception {
		log.info("received internal message for topic:" + topicIdentifier);
		ManagedZone zone = ManagedZoneUtil.getZoneForTopic(topicIdentifier);
		String msgPayload = message.toString();
		ManagedTopic topic = new ManagedTopic(topicIdentifier);

		switch (zone) {
		case CONFIG_PRIVATE:
		case CONFIG_GLOBAL:
			UserConfiguration msgConfig = getServices().getJsonParser().deserializeUserConfiguration(msgPayload);
			getServices().getConfigDb(zone).put(topic.getIdentifier(msgConfig, zone), msgConfig);
			log.info("received configuration message for topic: " + topicIdentifier);
			break;
		case QUOTA_PRIVATE:
		case QUOTA_GLOBAL:
			UserQuota msgQuota = getServices().getJsonParser().deserializeQuota(msgPayload);
			getServices().getQuotaDb(zone).put(topic.getIdentifier(msgQuota, zone), msgQuota, true);
			log.info("received quota message for topic: " + topicIdentifier);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean canDoOperation(AuthorizationProperties properties, MqttAction operation) {
		throw new FceSystemException("not implemented");
	}

}