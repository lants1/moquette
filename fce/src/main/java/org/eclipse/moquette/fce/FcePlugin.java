package org.eclipse.moquette.fce;

import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.eclipse.moquette.fce.common.FceTimeUtil;
import org.eclipse.moquette.fce.common.ManagedZone;
import org.eclipse.moquette.fce.event.PluginEventHandler;
import org.eclipse.moquette.fce.job.ManagedZoneGarbageCollector;
import org.eclipse.moquette.fce.job.QuotaUpdater;
import org.eclipse.moquette.fce.service.FceServiceFactory;
import org.eclipse.moquette.plugin.AuthenticationAndAuthorizationPlugin;
import org.eclipse.moquette.plugin.BrokerOperator;

public class FcePlugin implements AuthenticationAndAuthorizationPlugin {

	private final static Logger log = Logger.getLogger(FcePlugin.class.getName());
	private static final String PLUGIN_IDENTIFIER = "FCE-Plugin";

	private FceServiceFactory services;
	ScheduledExecutorService scheduler;

	@Override
	public void load(Properties config, BrokerOperator brokerOperator) {
		services = new FceServiceFactory(config, brokerOperator);

		scheduler = Executors.newScheduledThreadPool(2);
		scheduler.scheduleAtFixedRate(new QuotaUpdater(services), FceTimeUtil.delayTo(0, 0), 1, TimeUnit.HOURS);
		scheduler.scheduleAtFixedRate(new ManagedZoneGarbageCollector(services), 7, 7, TimeUnit.DAYS);

		services.getMqttDataStore().subscribe(ManagedZone.MANAGED_INTENT.getTopicFilter());
		services.getMqttDataStore().subscribe(ManagedZone.MANAGED_QUOTA.getTopicFilter());
		services.getMqttDataStore().subscribe(ManagedZone.MANAGED_CONFIGURATION.getTopicFilter());
		
		log.info(PLUGIN_IDENTIFIER + " loaded and scheduler started....");
	}

	@Override
	public void unload() {
		scheduler.shutdownNow();
		services.getMqttDataStore().unsubscribe(ManagedZone.MANAGED_INTENT.getTopicFilter());
		log.info(PLUGIN_IDENTIFIER + " unloaded");
	}

	@Override
	public boolean checkValid(String username, byte[] password) {
		PluginEventHandler handler = new PluginEventHandler(services);
		return handler.checkValid(username, password);
	}

	@Override
	public boolean canWrite(String topic, String user, String client) {
		PluginEventHandler handler = new PluginEventHandler(services);
		return handler.canWrite(topic, user, client);
	}

	@Override
	public boolean canRead(String topic, String user, String client) {
		PluginEventHandler handler = new PluginEventHandler(services);
		return handler.canRead(topic, user, client);
	}

	@Override
	public String getPluginIdentifier() {
		return PLUGIN_IDENTIFIER;
	}

}
