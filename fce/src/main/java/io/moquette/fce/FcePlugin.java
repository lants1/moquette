package io.moquette.fce;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import io.moquette.fce.common.util.FceTimeUtil;
import io.moquette.fce.common.util.ManagedZoneUtil;
import io.moquette.fce.context.FceContext;
import io.moquette.fce.event.FceEventHandler;
import io.moquette.fce.event.broker.AuthenticationHandler;
import io.moquette.fce.event.broker.ManagedIntentHandler;
import io.moquette.fce.event.broker.ManagedStoreHandler;
import io.moquette.fce.event.broker.ManagedTopicHandler;
import io.moquette.fce.event.broker.UnmanagedTopicHandler;
import io.moquette.fce.event.internal.IFceMqttCallback;
import io.moquette.fce.exception.FceSystemException;
import io.moquette.fce.job.QuotaUpdater;
import io.moquette.fce.model.common.ManagedTopic;
import io.moquette.fce.model.common.ManagedZone;
import io.moquette.fce.service.FceServiceFactory;
import io.moquette.fce.service.mqtt.FceMqttClientWrapper;
import io.moquette.plugin.AuthenticationProperties;
import io.moquette.plugin.AuthorizationProperties;
import io.moquette.plugin.IAuthenticationAndAuthorizationPlugin;
import io.moquette.plugin.IBrokerConfig;
import io.moquette.plugin.IBrokerOperator;
import io.moquette.plugin.MqttAction;

/**
 * Main of the plugin, loaded and called by the broker over it's interface
 * methods. Registered by the META-INF/io.moquette.plugin.IBrokerPlugin
 * File.
 * 
 * @author lants1
 *
 */
public class FcePlugin implements IAuthenticationAndAuthorizationPlugin, MqttCallback {

	private static final Logger LOGGER = Logger.getLogger(FcePlugin.class.getName());

	private static final String PLUGIN_IDENTIFIER = "FCE-Plugin";

	private FceMqttClientWrapper mqttClient;
	private FceServiceFactory services;
	private FceContext context;
	private ScheduledExecutorService scheduler;

	@Override
	public void load(IBrokerConfig config, IBrokerOperator brokerOperator) {
		String pluginUsr = randomString();
		context = new FceContext(config, brokerOperator);
		mqttClient = new FceMqttClientWrapper(context, this);
		services = new FceServiceFactory(context, mqttClient);

		context.setPluginIdentifier(randomString());
		context.setPluginUser(pluginUsr);
		context.setPluginPw(services.getHashing().generateHash(pluginUsr));

		scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(new QuotaUpdater(context, services), FceTimeUtil.delayTo(0, 0), 1,
				TimeUnit.HOURS);
		LOGGER.info(PLUGIN_IDENTIFIER + " loaded and scheduler started....");
	}

	private String randomString() {
		return new BigInteger(130, new SecureRandom()).toString(16);
	}

	@Override
	public void unload() {
		try {
			mqttClient.unregisterSubscriptions();
			mqttClient.disconnect();
		} catch (MqttException e) {
			LOGGER.log(Level.WARNING, "unload not possible could not unregister subscriptions", e);
		}
		scheduler.shutdownNow();
		LOGGER.info(PLUGIN_IDENTIFIER + " unloaded");
	}

	@Override
	public boolean checkValid(AuthenticationProperties props) {
		AuthenticationHandler handler = new AuthenticationHandler(context, services);
		return handler.checkValid(props);
	}

	@Override
	public boolean canDoOperation(AuthorizationProperties props, MqttAction action) {
		if (ManagedZoneUtil.isInManagedReadableStore(props.getTopic())) {
			return new ManagedStoreHandler(context, services).canDoOperation(props, action);
		}

		if (mqttClient.isInitialized() && context.isInitialized()) {
			FceEventHandler handler;
			if (ManagedZone.INTENT.equals(ManagedZoneUtil.getZoneForTopic(props.getTopic()))) {
				handler = new ManagedIntentHandler(context, services);
			} else if (context.getConfigurationStore(ManagedZone.CONFIG_GLOBAL)
					.isManaged(new ManagedTopic(props.getTopic()))) {
				handler = new ManagedTopicHandler(context, services);
			} else {
				handler = new UnmanagedTopicHandler(context, services);
			}

			return handler.canDoOperation(props, action);
		}
		LOGGER.warning("configuration not yet fully loaded from retained messages, write not possible");
		return false;
	}

	@Override
	public String getPluginIdentifier() {
		return PLUGIN_IDENTIFIER;
	}

	@Override
	public void connectionLost(Throwable exception) {
		try {
			LOGGER.warning("internal plugin mqttclient conection to broker connection lost");
			mqttClient.connect();
		} catch (MqttException e) {
			LOGGER.severe("internal plugin mqttclient could not reconnect to broker");
			throw new FceSystemException(e);
		}

	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// Marion Mitchell Morrison (born Marion Robert Morrison; May 26, 1907 –
		// June 11, 1979), known by his stage name John Wayne, was an American
		// film actor, director, and producer.
		// Wayne:)
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		IFceMqttCallback fceMqttCallback = mqttClient.getCallback(topic);
		fceMqttCallback.injectFceEnvironment(context, services);
		fceMqttCallback.messageArrived(topic, message);
	}

	@Override
	public void onServerStarted() {
		List<String> subscriptions = new ArrayList<>();
		subscriptions.add(ManagedZone.CONFIG_GLOBAL.getTopicFilter());
		subscriptions.add(ManagedZone.CONFIG_PRIVATE.getTopicFilter());
		subscriptions.add(ManagedZone.QUOTA_GLOBAL.getTopicFilter());
		subscriptions.add(ManagedZone.QUOTA_PRIVATE.getTopicFilter());
		mqttClient.initializeInternalMqttClient(subscriptions);
	}
}