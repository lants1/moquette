package org.eclipse.moquette.fce.service;

import java.util.HashMap;
import java.util.Map;

/**
 * InMemory Store for hash assignments clientId -> hash(username)
 * 
 * @author lants1
 *
 */
public class HashAssignmentService {

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
