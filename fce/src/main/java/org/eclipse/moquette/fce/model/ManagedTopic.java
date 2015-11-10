package org.eclipse.moquette.fce.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for a ManageTopic which contains all UserConfigurations.
 * 
 * @author lants1
 *
 */
public class ManagedTopic {

	private String topicIdentifier;
	private List<UserConfiguration> userConfigurations;

	public ManagedTopic(String topicIdentifier){
		this.userConfigurations = new ArrayList<UserConfiguration>();
	}
	
	public ManagedTopic(String topicIdentifier, List<UserConfiguration> userConfigurations) {
		this.topicIdentifier = topicIdentifier;
		this.userConfigurations = userConfigurations;
	}

	public List<UserConfiguration> getUserConfigurations() {
		return userConfigurations;
	}

	public void setUserConfigurations(List<UserConfiguration> userConfigurations) {
		this.userConfigurations = userConfigurations;
	}

	public String getTopicIdentifier() {
		return topicIdentifier;
	}

	public void setTopicIdentifier(String topicIdentifier) {
		this.topicIdentifier = topicIdentifier;
	}
}
