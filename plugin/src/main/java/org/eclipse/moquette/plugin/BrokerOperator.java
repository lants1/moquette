package org.eclipse.moquette.plugin;

/**
 * 
 * Interface for internal broker operations which can be used by plugins.
 * 
 * @author lants1
 *
 */
public interface BrokerOperator {
	
	public int countRetainedMessages(String topicFilter);
	
}
