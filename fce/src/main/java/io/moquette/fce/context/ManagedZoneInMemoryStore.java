package io.moquette.fce.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import io.moquette.fce.common.util.ManagedZoneUtil;
import io.moquette.fce.exception.FceAuthorizationException;
import io.moquette.fce.model.ManagedInformation;
import io.moquette.fce.model.common.ManagedTopic;
import io.moquette.fce.model.common.ManagedZone;
import io.moquette.plugin.IBrokerOperator;
import io.moquette.plugin.MqttAction;

/**
 * Abstract class which implements common operations for InMemory Mqtt datastores...
 * 
 * @author lants1
 *
 */
public abstract class ManagedZoneInMemoryStore {
	
	private final static Logger log = Logger.getLogger(ManagedZoneInMemoryStore.class.getName());
	
	private boolean initialized;
	private final IBrokerOperator brokerOperator;
	private final ManagedZone correspondingZone;
	
	public ManagedZoneInMemoryStore(IBrokerOperator brokerOperator, ManagedZone correspondingZone) {
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

	public ManagedZone getZone() {
		return correspondingZone;
	}
	
	protected ManagedInformation getManagedInformation(String topicStr, String usernameHash, MqttAction operation)
			throws FceAuthorizationException {
		ManagedTopic topic = new ManagedTopic(topicStr);
		String reducedTopicFilter = topic.getIdentifer();
		while (!reducedTopicFilter.isEmpty()) {
			ManagedInformation userConfig = get(new ManagedTopic(reducedTopicFilter), usernameHash, operation);
			if (userConfig != null) {
				return userConfig;
			}
			reducedTopicFilter = StringUtils.substringBeforeLast(reducedTopicFilter, "/");
		}
		return null;
	}

	public boolean isManaged(ManagedTopic topic) {
		List<String> tokens = getTokens(topic);

		for (Map.Entry<String, ?> entry : getStore().entrySet()) {

			String key = entry.getKey();
			for (String token : tokens) {
				if (key.startsWith(token)) {
					return true;
				}
			}
		}
		return false;
	}

	protected List<String> getTokens(ManagedTopic topic) {
		List<String> tokens = new ArrayList<>();
		String reducedTopicFilter = topic.getIdentifer();
		while (!reducedTopicFilter.isEmpty()) {
			tokens.add(ManagedZoneUtil.moveTopicToZone(reducedTopicFilter, getZone()));
			reducedTopicFilter = StringUtils.substringBeforeLast(reducedTopicFilter, "/");
		}
		return tokens;
	}

	abstract protected HashMap<String, ?> getStore();

	abstract protected ManagedInformation get(ManagedTopic topic, String usernameHash, MqttAction operation);
}
