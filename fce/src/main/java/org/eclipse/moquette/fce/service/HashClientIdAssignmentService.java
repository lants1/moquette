package org.eclipse.moquette.fce.service;

import java.util.HashMap;
import java.util.Map;

public class HashClientIdAssignmentService {

	Map<String, String> assignments = new HashMap<>();
	
	public void put(String clientId, String hash){
		assignments.put(clientId, hash);
	}
	
	public void get(String clientId){
		assignments.get(clientId);
	}
	
}
