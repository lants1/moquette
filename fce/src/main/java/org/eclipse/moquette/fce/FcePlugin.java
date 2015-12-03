package org.eclipse.moquette.fce;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.moquette.fce.common.util.FceTimeUtil;
import org.eclipse.moquette.fce.common.util.ManagedZoneUtil;
import org.eclipse.moquette.fce.context.FceContext;
import org.eclipse.moquette.fce.event.AuthenticationHandler;
import org.eclipse.moquette.fce.event.FceEventHandler;
import org.eclipse.moquette.fce.event.ManagedIntentHandler;
import org.eclipse.moquette.fce.event.ManagedStoreHandler;
import org.eclipse.moquette.fce.event.ManagedTopicHandler;
import org.eclipse.moquette.fce.event.UnmanagedTopicHandler;
import org.eclipse.moquette.fce.job.Heartbeat;
import org.eclipse.moquette.fce.job.QuotaUpdater;
import org.eclipse.moquette.fce.model.common.ManagedTopic;
import org.eclipse.moquette.fce.model.common.ManagedZone;
import org.eclipse.moquette.fce.service.FceServiceFactory;
import org.eclipse.moquette.plugin.IAuthenticationAndAuthorizationPlugin;
import org.eclipse.moquette.plugin.IBrokerConfig;
import org.eclipse.moquette.plugin.AuthenticationProperties;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.IBrokerOperator;
import org.eclipse.moquette.plugin.MqttAction;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Main of the plugin, loaded and called by the broker over it's interface methods. Registered
 * by the META-INF/org.eclipse.moquette.plugin.IBrokerPlugin File.
 * 
 * @author lants1
 *
 */
public class FcePlugin implements IAuthenticationAndAuthorizationPlugin, MqttCallback {

	private final static Logger log = Logger.getLogger(FcePlugin.class.getName());
	
	private static final String PLUGIN_IDENTIFIER = "FCE-Plugin";

	private FceServiceFactory services;
	private FceContext context;
	private ScheduledExecutorService scheduler;
	
	@Override
	public void load(IBrokerConfig config, IBrokerOperator brokerOperator) {
		String pluginUsr = randomString();
		context = new FceContext(config, brokerOperator);
		services = new FceServiceFactory(context);

		context.setPluginIdentifier(randomString());
		context.setPluginUser(pluginUsr);
		context.setPluginPw(services.getHashing().generateHash(pluginUsr));
		
		scheduler = Executors.newScheduledThreadPool(2);
		scheduler.scheduleAtFixedRate(new QuotaUpdater(context, services), FceTimeUtil.delayTo(0, 0), 1, TimeUnit.HOURS);
		scheduler.scheduleAtFixedRate(new Heartbeat(context, services), 3, 60, TimeUnit.SECONDS);
		log.info(PLUGIN_IDENTIFIER + " loaded and scheduler started....");
	}

	private String randomString() {
		return new BigInteger(130, new SecureRandom()).toString(16);
	}

	@Override
	public void unload() {
		try {
			services.getMqtt().unregisterManageSubscriptions();
		} catch (MqttException e) {
			log.log(Level.WARNING, "unload not possible could not unregister subscriptions", e);
		}
		scheduler.shutdownNow();
		log.info(PLUGIN_IDENTIFIER + " unloaded");
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

		if (services.isInitialized()) {
			FceEventHandler handler;
			if (ManagedZone.INTENT.equals(ManagedZoneUtil.getZoneForTopic(props.getTopic()))) {
				handler = new ManagedIntentHandler(context, services);
			} else if (context.getConfigurationStore(ManagedZone.CONFIG_GLOBAL).isManaged(new ManagedTopic(props.getTopic()))) {
				handler = new ManagedTopicHandler(context, services);
			} else {
				handler = new UnmanagedTopicHandler(context, services);
			}

			return handler.canDoOperation(props, action);
		}
		log.warning("configuration not yet fully loaded from retained messages, write not possible");
		return false;
	}

	
	
	@Override
	public String getPluginIdentifier() {
		return PLUGIN_IDENTIFIER;
	}

	@Override
	public void connectionLost(Throwable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void messageArrived(String arg0, MqttMessage arg1) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
