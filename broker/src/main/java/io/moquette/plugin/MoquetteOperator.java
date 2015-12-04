package io.moquette.plugin;

import java.util.Collection;

import io.moquette.plugin.IBrokerOperator;
import io.moquette.spi.IMatchingCondition;
import io.moquette.spi.IMessagesStore;
import io.moquette.spi.impl.ProtocolProcessor;
import io.moquette.spi.impl.subscriptions.SubscriptionsStore;

public class MoquetteOperator implements IBrokerOperator {

	final ProtocolProcessor processor;
	
	public MoquetteOperator(final ProtocolProcessor processor) {
		super();
		this.processor = processor;
	}

	@Override
	public int countRetainedMessages(final String topicFilter) {
		
        Collection<IMessagesStore.StoredMessage> messages = processor.getMessagesStore().searchMatching(new IMatchingCondition() {
            public boolean match(String key) {
                return SubscriptionsStore.matchTopics(key, topicFilter);
            }
        });

		return messages.size();
	}


}
