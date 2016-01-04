package io.moquette.plugin;

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
	 * @param topicFilter String
	 * @return count of retained messages within the given topicFilter Parameter as int
	 */
	public int countRetainedMessages(String topicFilter);
	
}
