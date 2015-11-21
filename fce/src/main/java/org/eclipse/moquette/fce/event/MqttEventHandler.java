package org.eclipse.moquette.fce.event;

import java.util.List;
import java.util.logging.Logger;

import org.eclipse.moquette.fce.common.ManagedZone;
import org.eclipse.moquette.fce.common.ManagedZoneUtil;
import org.eclipse.moquette.fce.common.converter.ModelConverter;
import org.eclipse.moquette.fce.exception.FceSystemFailureException;
import org.eclipse.moquette.fce.model.ManagedTopic;
import org.eclipse.moquette.fce.model.configuration.UserConfiguration;
import org.eclipse.moquette.fce.model.quota.Quota;
import org.eclipse.moquette.fce.model.quota.UserQuotaData;
import org.eclipse.moquette.fce.service.FceServiceFactory;
import org.eclipse.moquette.plugin.MqttOperation;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttEventHandler implements MqttCallback {

	private final static Logger log = Logger.getLogger(MqttEventHandler.class.getName());

	FceServiceFactory services;

	public MqttEventHandler(FceServiceFactory services) {
		this.services = services;
	}

	@Override
	public void connectionLost(Throwable arg0) {
		try {
			log.warning("internal plugin mqttclient conection to broker connection lost");
			services.getMqttService().connect();
		} catch (MqttException e) {
			throw new FceSystemFailureException(e);
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// doNothing
	}

	// TODO lants1 better implementation
	@Override
	public void messageArrived(String topicIdentifier, MqttMessage message) throws Exception {
		ManagedZone topicZone = ManagedZoneUtil.getZoneForTopic(topicIdentifier);
		String msgPayload = String.valueOf(message.getPayload());
		ManagedTopic topic = new ManagedTopic(topicIdentifier);

		switch (topicZone) {
		case MANAGED_INTENT:
			UserConfiguration msgIntent = services.getJsonParser().deserializeUserConfiguration(msgPayload);
	
			services.getConfigDbService().put(topic.getUserTopicIdentifier(msgIntent, topicZone), msgIntent);
			services.getMqttService().publish(topic.getUserTopicIdentifier(msgIntent, topicZone), msgPayload, true);
			
			List<Quota> subscribeState = ModelConverter.convertRestrictionsToQuotas(msgIntent.getSubscribeRestrictions());
			List<Quota> publishState = ModelConverter.convertRestrictionsToQuotas(msgIntent.getPublishRestrictions());
			UserQuotaData subscribeQuotas = new UserQuotaData(msgIntent.getUserName(), msgIntent.getUserIdentifier(), subscribeState);
			UserQuotaData publishQuotas = new UserQuotaData(msgIntent.getUserName(), msgIntent.getUserIdentifier(), publishState);
		
			String subscribeQuotasTopic = topic.getUserTopicIdentifier(subscribeQuotas, ManagedZone.MANAGED_QUOTA, MqttOperation.SUBSCRIBE);
			String publishQuotasTopic = topic.getUserTopicIdentifier(publishQuotas, ManagedZone.MANAGED_QUOTA, MqttOperation.PUBLISH);
			
			services.getQuotaDbService().put(subscribeQuotasTopic, subscribeQuotas, true);
			services.getQuotaDbService().put(publishQuotasTopic, publishQuotas, true);
			services.getMqttService().publish(subscribeQuotasTopic, services.getJsonParser().serialize(subscribeQuotas), true);
			services.getMqttService().publish(publishQuotasTopic, services.getJsonParser().serialize(publishQuotas), true);
			
			log.fine("received configuration message for topic: " + topicIdentifier);
			break;
		case MANAGED_CONFIGURATION:
			if (!services.isInitialized()) {
				UserConfiguration msgConfig = services.getJsonParser().deserializeUserConfiguration(msgPayload);
				services.getConfigDbService().put(topic.getUserTopicIdentifier(msgConfig, topicZone), msgConfig);
				services.getMqttService().publish(topic.getUserTopicIdentifier(msgConfig, topicZone), msgPayload, true);
				log.fine("received configuration message for topic: " + topicIdentifier);
			}
			break;
		case MANAGED_QUOTA:
			if (!services.isInitialized()) {
				UserQuotaData msgQuota = services.getJsonParser().deserializeQuota(msgPayload);
				services.getQuotaDbService().put(topic.getUserTopicIdentifier(msgQuota, topicZone), msgQuota, true);
				services.getMqttService().publish(topic.getUserTopicIdentifier(msgQuota, topicZone), msgPayload, true);
				log.fine("received quota message for topic: " + topicIdentifier);
			}
			break;
		default:
			break;
		}

	}

}
