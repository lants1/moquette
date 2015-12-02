package org.eclipse.moquette.fce;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.moquette.fce.common.util.FceHashUtil;
import org.eclipse.moquette.fce.common.util.FceTimeUtil;
import org.eclipse.moquette.fce.common.util.ManagedZoneUtil;
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
import org.eclipse.moquette.fce.service.IFceServiceFactory;
import org.eclipse.moquette.fce.service.FceServiceFactoryImpl;
import org.eclipse.moquette.plugin.IAuthenticationAndAuthorizationPlugin;
import org.eclipse.moquette.plugin.IBrokerConfig;
import org.eclipse.moquette.plugin.AuthenticationProperties;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.IBrokerOperator;
import org.eclipse.moquette.plugin.MqttAction;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 * Main of the plugin, loaded and called by the broker over it's interface methods. Registered
 * by the META-INF/org.eclipse.moquette.plugin.IBrokerPlugin File.
 * 
 * @author lants1
 *
 */
public class FcePlugin implements IAuthenticationAndAuthorizationPlugin {

	private final static Logger log = Logger.getLogger(FcePlugin.class.getName());
	private static final String PLUGIN_IDENTIFIER = "FCE-Plugin";

	public static final String PROPS_PLUGIN_KEY_MANAGER_PASSWORD = "plugin_key_manager_password";
	public static final String PROPS_PLUGIN_KEY_STORE_PASSWORD = "plugin_key_store_password";
	public static final String PROPS_PLUGIN_JKS_PATH = "plugin_jks_path";

	public static final String PROPS_PLUGIN_CLIENT_USERNAME = "plugin_client_username";
	public static final String PROPS_PLUGIN_CLIENT_PASSWORD = "plugin_client_password";
	public static final String PROPS_PLUGIN_CLIENT_IDENTIFIER = "plugin_client_identifier";

	private SecureRandom random = new SecureRandom();

	private String pluginIdentifier;
	private IFceServiceFactory services;
	ScheduledExecutorService scheduler;
	
	@Override
	public void load(IBrokerConfig config, IBrokerOperator brokerOperator) {
		String pluginUsr = randomString();
		String pluginPw = FceHashUtil.getFceHash(pluginUsr);
		pluginIdentifier = randomString();

		config.setProperty(PROPS_PLUGIN_CLIENT_IDENTIFIER, pluginIdentifier);
		config.setProperty(PROPS_PLUGIN_CLIENT_USERNAME, pluginUsr);
		config.setProperty(PROPS_PLUGIN_CLIENT_PASSWORD, pluginPw);

		services = new FceServiceFactoryImpl(config, brokerOperator);

		scheduler = Executors.newScheduledThreadPool(2);
		scheduler.scheduleAtFixedRate(new QuotaUpdater(services), FceTimeUtil.delayTo(0, 0), 1, TimeUnit.HOURS);
		scheduler.scheduleAtFixedRate(new Heartbeat(services), 3, 60, TimeUnit.SECONDS);
		log.info(PLUGIN_IDENTIFIER + " loaded and scheduler started....");
	}

	private String randomString() {
		return new BigInteger(130, random).toString(16);
	}

	@Override
	public void unload() {
		try {
			services.getMqtt().unregisterSubscriptions();
		} catch (MqttException e) {
			log.log(Level.WARNING, "unload not possible could not unregister subscriptions", e);
		}
		scheduler.shutdownNow();
		log.info(PLUGIN_IDENTIFIER + " unloaded");
	}

	@Override
	public boolean checkValid(AuthenticationProperties props) {
		AuthenticationHandler handler = new AuthenticationHandler(services, pluginIdentifier);
		return handler.checkValid(props);
	}

	@Override
	public boolean canDoOperation(AuthorizationProperties props, MqttAction action) {
		if (ManagedZoneUtil.isInManagedReadableStore(props.getTopic())) {
			return new ManagedStoreHandler(services, pluginIdentifier).canDoOperation(props, action);
		}

		if (services.isInitialized()) {
			FceEventHandler handler;
			if (ManagedZone.INTENT.equals(ManagedZoneUtil.getZoneForTopic(props.getTopic()))) {
				handler = new ManagedIntentHandler(services, pluginIdentifier);
			} else if (services.getConfigDb(ManagedZone.CONFIG_GLOBAL).isManaged(new ManagedTopic(props.getTopic()))) {
				handler = new ManagedTopicHandler(services, pluginIdentifier);
			} else {
				handler = new UnmanagedTopicHandler(services, pluginIdentifier);
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

}
