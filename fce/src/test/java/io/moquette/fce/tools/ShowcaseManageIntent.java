package io.moquette.fce.tools;

import io.moquette.fce.common.ReadFileUtil;
import io.moquette.fce.model.common.ManagedZone;

public class ShowcaseManageIntent extends Showcase{

	private static String USERNAME = "user";
	private static String TOPIC = "/test2";
	private static String INTENT_TOPIC = ManagedZone.INTENT.getTopicPrefix()+TOPIC;
	private static String INFO_TOPIC = ManagedZone.INFO.getTopicPrefix()+TOPIC+"/#";

	public static void main(String[] args) throws Exception {
		client1 = initializeInternalMqttClient(USERNAME);
		client1.subscribe(INFO_TOPIC);
		client1.subscribe(TOPIC);
		String inputJson = ReadFileUtil.readFileString("/fce/showcase_manage.json");
		
		client1.publish(INTENT_TOPIC, inputJson.getBytes(), Showcase.FIRE_AND_FORGET, true);
		
		Thread.sleep(2000);
		client1.publish(TOPIC, "test".getBytes(), Showcase.FIRE_AND_FORGET, true);
		Thread.sleep(2000);
		client1.publish(TOPIC, "test".getBytes(), Showcase.FIRE_AND_FORGET, true);
		Thread.sleep(2000);
		client1.publish(TOPIC, "test".getBytes(), Showcase.FIRE_AND_FORGET, true);
		Thread.sleep(2000);
		client1.publish(TOPIC, "test".getBytes(), Showcase.FIRE_AND_FORGET, true); // quota depleted
		
		disconnectClients();
	}
}
