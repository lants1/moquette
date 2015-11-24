package org.eclipse.moquette.fce.event;

import java.util.logging.Logger;

import org.eclipse.moquette.fce.common.ManagedZoneUtil;
import org.eclipse.moquette.fce.model.ManagedTopic;
import org.eclipse.moquette.fce.service.IFceServiceFactory;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.MqttAction;

public class ManagedStoreHandler extends FceEventHandler {

	private final static Logger log = Logger.getLogger(ManagedStoreHandler.class.getName());

	public ManagedStoreHandler(IFceServiceFactory services, String pluginClientIdentifier) {
		super(services, pluginClientIdentifier);
	}

	public boolean canDoOperation(AuthorizationProperties properties, MqttAction action) {
		Boolean preCheckState = preCheckManagedZone(properties, action);
		
		if(preCheckState != null){
			return preCheckState;
		}

		ManagedTopic topic = new ManagedTopic(ManagedZoneUtil.removeZoneIdentifier(properties.getTopic()));

		return topic.isAllowedForUser(properties);
	}

}
