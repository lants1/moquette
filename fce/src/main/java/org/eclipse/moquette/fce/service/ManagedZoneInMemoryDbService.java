package org.eclipse.moquette.fce.service;

import java.util.HashMap;

import org.eclipse.moquette.fce.common.ManagedZone;
import org.eclipse.moquette.plugin.BrokerOperator;

public abstract class ManagedZoneInMemoryDbService {
	
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
			}
		}
		return initialized;
	}
	
	abstract protected HashMap<String, ?> getStore();
}
