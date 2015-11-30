package org.eclipse.moquette.plugin;

/**
 * 
 * Interface for internal broker operations which can be used by plugins.
 * 
 * @author lants1
 *
 */
public interface IBrokerOperator {
	
	/** 
	 * Counts retained messages in a area of the broker.
	 * 
	 * @param String topicFilter
	 * @return int count of retained messages within the given topicFilter Parameter
	 */
	public int countRetainedMessages(String topicFilter);
	
}
