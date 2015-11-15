package org.eclipse.moquette.plugin;

import java.util.Collection;

import org.eclipse.moquette.spi.IMatchingCondition;
import org.eclipse.moquette.spi.IMessagesStore;
import org.eclipse.moquette.spi.impl.ProtocolProcessor;
import org.eclipse.moquette.spi.impl.subscriptions.SubscriptionsStore;

public class MoquetteOperator implements BrokerOperator {

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
