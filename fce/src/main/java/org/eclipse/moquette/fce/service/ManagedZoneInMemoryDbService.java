package org.eclipse.moquette.fce.service;

import java.util.HashMap;
import java.util.logging.Logger;

import org.eclipse.moquette.fce.common.ManagedZone;
import org.eclipse.moquette.plugin.BrokerOperator;

public abstract class ManagedZoneInMemoryDbService {
	
	private final static Logger log = Logger.getLogger(ManagedZoneInMemoryDbService.class.getName());
	
	private boolean initialized;
	private BrokerOperator brokerOperator;
	private ManagedZone correspondingZone;
	
	public ManagedZoneInMemoryDbService(BrokerOperator brokerOperator, ManagedZone correspondingZone) {
		super();
		this.initialized = false;
		this.brokerOperator = brokerOperator;
		this.correspondingZone = correspondingZone;
	}
	
	public boolean isInitialized() {
		if (!initialized) {
			if (getStore().size() >= brokerOperator
					.countRetainedMessages(correspondingZone.getTopicFilter())) {
				initialized = true;
				log.info(correspondingZone.name() + "In-Memory DB initialized");
			}
		}
		return initialized;
	}
	
	abstract protected HashMap<String, ?> getStore();

	public ManagedZone getZone() {
		return correspondingZone;
	}
	
}
