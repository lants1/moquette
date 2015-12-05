package io.moquette.fce.event.broker;

import java.util.logging.Logger;

import io.moquette.fce.common.util.ManagedZoneUtil;
import io.moquette.fce.context.FceContext;
import io.moquette.fce.event.FceEventHandler;
import io.moquette.fce.model.common.CheckResult;
import io.moquette.fce.model.common.ManagedTopic;
import io.moquette.fce.service.FceServiceFactory;
import io.moquette.plugin.AuthorizationProperties;
import io.moquette.plugin.MqttAction;

/**
 * Handler which is used when the request goes into the plugin store on the broker.
 * 
 * @author lants1
 *
 */
public class ManagedStoreHandler extends FceEventHandler {

	private static final Logger LOGGER = Logger.getLogger(ManagedStoreHandler.class.getName());

	public ManagedStoreHandler(FceContext context, FceServiceFactory services) {
		super(context, services);
	}

	@Override
	public boolean canDoOperation(AuthorizationProperties props, MqttAction action) {
		LOGGER.info("store-event on:" + props.getTopic() + "from client:" + props.getClientId() + " and action:" + action);

		CheckResult preCheckState = preCheckManagedZone(props, action);

		if(!CheckResult.NO_RESULT.equals(preCheckState)){
			return preCheckState.getValue();
		}

		ManagedTopic topic = new ManagedTopic(ManagedZoneUtil.removeZoneIdentifier(props.getTopic()));
		boolean result = topic.isAllowedForUser(getContext().getHashAssignment().get(props.getClientId()));

		LOGGER.info("store-event result on:" + props.getTopic() + "from client:" + props.getClientId() + " and action:"
				+ action + " is:" + result);
		return result;
	}

}
