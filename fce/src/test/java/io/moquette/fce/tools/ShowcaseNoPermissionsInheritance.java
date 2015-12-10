package io.moquette.fce.tools;

import io.moquette.fce.common.ReadFileUtil;
import io.moquette.fce.model.common.ManagedZone;

public class ShowcaseNoPermissionsInheritance extends Showcase{

	private static final String SUBTOPIC = "/test4/bla";
	private static final String TOPIC = "/test4";
	private static String USERNAME_WRONG = "dfasfsdf";

	public static void main(String[] args) throws Exception {
		client1 = initializeInternalMqttClient(USERNAME_WRONG);
		
		client1.subscribe(TOPIC);
		client1.subscribe(ManagedZone.INFO.getTopicPrefix()+TOPIC+"/#");
		
		String inputJson = ReadFileUtil.readFileString("/fce/showcase_manage.json");
		client1.publish(ManagedZone.INTENT.getTopicPrefix()+TOPIC, inputJson.getBytes(), Showcase.FIRE_AND_FORGET, true);
		
		Thread.sleep(2000);
		client1.publish(TOPIC, "test".getBytes(), Showcase.FIRE_AND_FORGET, true);
		Thread.sleep(2000);
		client1.publish(SUBTOPIC, "test".getBytes(), Showcase.FIRE_AND_FORGET, true);
		Thread.sleep(2000);
		client1.publish(TOPIC, "test".getBytes(), Showcase.FIRE_AND_FORGET, true);
		Thread.sleep(2000);
		client1.publish(SUBTOPIC, "test".getBytes(), Showcase.FIRE_AND_FORGET, true); // quota depleted*/
		Thread.sleep(2000);
		client1.publish(TOPIC, "test".getBytes(), Showcase.FIRE_AND_FORGET, true); //  quota depleted*/
		
		disconnectClients();
	}

}
