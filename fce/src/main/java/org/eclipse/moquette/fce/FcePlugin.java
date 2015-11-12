package org.eclipse.moquette.fce;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.eclipse.moquette.fce.job.ManagedZoneGarbageCollector;
import org.eclipse.moquette.fce.job.PeriodicQuotaCleaner;
import org.eclipse.moquette.fce.service.FceAuthorizationService;
import org.eclipse.moquette.fce.service.MqttDataStoreService;
import org.eclipse.moquette.plugin.AuthenticationAndAuthorizationPlugin;
import org.eclipse.moquette.plugin.BrokerOperator;

public class FcePlugin implements AuthenticationAndAuthorizationPlugin {

	private static final String PLUGIN_IDENTIFIER = "FCE-Plugin";

	private final static Logger log = Logger.getLogger(FcePlugin.class.getName()); 
	
	MqttDataStoreService dataStore;
	FceAuthorizationService authorizationService;

	@Override
	public void load(Properties config, BrokerOperator brokerOperator) {
		this.dataStore = new MqttDataStoreService(config);
		dataStore.initializeDataStore();

		this.authorizationService = new FceAuthorizationService();
		
		ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(2);
		scheduledPool.scheduleAtFixedRate(new PeriodicQuotaCleaner(), computeNextDelay(0, 0), 1, TimeUnit.HOURS);
		scheduledPool.scheduleAtFixedRate(new ManagedZoneGarbageCollector(), 7, 7, TimeUnit.DAYS);
		log.info("plugin loaded and scheduler started....");
	}

	@Override
	public void unload() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean checkValid(String username, byte[] password) {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public boolean canWrite(String topic, String user, String client) {
		return authorizationService.getBasicPermission(topic).isWriteable();
	}

	@Override
	public boolean canRead(String topic, String user, String client) {
		return authorizationService.getBasicPermission(topic).isReadable();
	}
	
	@Override
	public String getPluginIdentifier() {
		return PLUGIN_IDENTIFIER;
	}

	private long computeNextDelay(int targetMin, int targetSec) {
		LocalDateTime localNow = LocalDateTime.now();
		ZoneId currentZone = ZoneId.systemDefault();
		ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
		ZonedDateTime zonedNextTarget = zonedNow.withMinute(targetMin).withSecond(targetSec);
		if (zonedNow.compareTo(zonedNextTarget) > 0) {
			zonedNextTarget = zonedNextTarget.plusDays(1);
		}

		Duration duration = Duration.between(zonedNow, zonedNextTarget);
		return duration.getSeconds();
	}
}
