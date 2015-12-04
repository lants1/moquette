package io.moquette.fce.context;

import java.util.HashMap;
import java.util.Map;

/**
 * InMemory Store for hash assignments clientId -> hash(username)
 * 
 * @author lants1
 *
 */
public class HashAssignmentStore {

	Map<String, String> assignments = new HashMap<>();
	
	public void put(String clientId, String hash){
		assignments.put(clientId, hash);
	}
	
	public String get(String clientId){
		return assignments.get(clientId);
	}
	
	public void remove(String clientId){
		assignments.remove(clientId);
	}
	
	
}
