package org.eclipse.moquette.fce.context;

import java.util.HashMap;
import java.util.Map;

/**
 * InMemory Store for topic to schema assignments topic -> msg
 * 
 * @author lants1
 *
 */
public class SchemaAssignmentStore {

	Map<String, byte[]> assignments = new HashMap<>();
	
	public void put(String topic, byte[] bs){
		assignments.put(topic, bs);
	}
	
	public byte[] get(String topic){
		return assignments.get(topic);
	}
	
	public void remove(String topic){
		assignments.remove(topic);
	}
	
	public boolean containsKey(String topic){
		return assignments.containsKey(topic);
	}
	
}
