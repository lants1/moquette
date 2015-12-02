package org.eclipse.moquette.fce.event;

import java.util.logging.Logger;

import org.eclipse.moquette.fce.common.util.ManagedZoneUtil;
import org.eclipse.moquette.fce.model.common.ManagedTopic;
import org.eclipse.moquette.fce.service.IFceServiceFactory;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.MqttAction;

/**
 * Handler which is used when the request goes into the plugin store on the broker.
 * 
 * @author lants1
 *
 */
public class ManagedStoreHandler extends FceEventHandler {

	private final static Logger log = Logger.getLogger(ManagedStoreHandler.class.getName());

	public ManagedStoreHandler(IFceServiceFactory services, String pluginClientIdentifier) {
		super(services, pluginClientIdentifier);
	}

	public boolean canDoOperation(AuthorizationProperties props, MqttAction action) {
		log.info("store-event on:" + props.getTopic() + "from client:" + props.getClientId() + " and action:" + action);

		Boolean preCheckState = preCheckManagedZone(props, action);

		if (preCheckState != null) {
			return preCheckState;
		}

		ManagedTopic topic = new ManagedTopic(ManagedZoneUtil.removeZoneIdentifier(props.getTopic()));
		boolean result = topic.isAllowedForUser(getServices().getHashAssignment().get(props.getClientId()));

		log.info("store-event result on:" + props.getTopic() + "from client:" + props.getClientId() + " and action:"
				+ action + " is:" + result);
		return result;
	}

}
