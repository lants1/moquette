package org.eclipse.moquette.fce;

import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.eclipse.moquette.fce.common.FceTimeUtil;
import org.eclipse.moquette.fce.common.ManagedZone;
import org.eclipse.moquette.fce.event.PluginEventHandler;
import org.eclipse.moquette.fce.job.QuotaUpdater;
import org.eclipse.moquette.fce.service.FceServiceFactory;
import org.eclipse.moquette.fce.service.FceServiceFactoryImpl;
import org.eclipse.moquette.plugin.AuthenticationAndAuthorizationPlugin;
import org.eclipse.moquette.plugin.BrokerOperator;

public class FcePlugin implements AuthenticationAndAuthorizationPlugin {

	private final static Logger log = Logger.getLogger(FcePlugin.class.getName());
	private static final String PLUGIN_IDENTIFIER = "FCE-Plugin";

	private FceServiceFactory serviceFactory;
	ScheduledExecutorService scheduler;

	@Override
	public void load(Properties config, BrokerOperator brokerOperator) {
		serviceFactory = new FceServiceFactoryImpl(config, brokerOperator);

		scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(new QuotaUpdater(serviceFactory), FceTimeUtil.delayTo(0, 0), 1, TimeUnit.HOURS);

		serviceFactory.getMqttService().subscribe(ManagedZone.MANAGED_INTENT.getTopicFilter());
		serviceFactory.getMqttService().subscribe(ManagedZone.MANAGED_QUOTA.getTopicFilter());
		serviceFactory.getMqttService().subscribe(ManagedZone.MANAGED_CONFIGURATION.getTopicFilter());

		log.info(PLUGIN_IDENTIFIER + " loaded and scheduler started....");
	}

	@Override
	public void unload() {
		scheduler.shutdownNow();
		serviceFactory.getMqttService().unsubscribe(ManagedZone.MANAGED_INTENT.getTopicFilter());
		log.info(PLUGIN_IDENTIFIER + " unloaded");
	}

	@Override
	public boolean checkValid(String username, byte[] password) {
		if (serviceFactory.isInitialized()) {
			PluginEventHandler handler = new PluginEventHandler(serviceFactory);
			return handler.checkValid(username, password);
		}
		log.info("configuration not yet fully loaded from retained messages, validity check not possible");
		return false;
	}

	@Override
	public boolean canWrite(String topic, String user, String client) {
		if (serviceFactory.isInitialized()) {
			PluginEventHandler handler = new PluginEventHandler(serviceFactory);
			return handler.canWrite(topic, user, client);
		}
		log.info("configuration not yet fully loaded from retained messages, write not possible");
		return false;
	}

	@Override
	public boolean canRead(String topic, String user, String client) {
		if (serviceFactory.isInitialized()) {
			PluginEventHandler handler = new PluginEventHandler(serviceFactory);
			return handler.canRead(topic, user, client);
		}
		log.info("configuration not yet fully loaded from retained messages, read not possible");
		return false;
	}

	@Override
	public String getPluginIdentifier() {
		return PLUGIN_IDENTIFIER;
	}

}
