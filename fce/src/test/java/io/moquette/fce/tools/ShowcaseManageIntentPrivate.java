package io.moquette.fce.tools;

import io.moquette.fce.common.ReadFileUtil;
import io.moquette.fce.model.common.ManagedZone;

public class ShowcaseManageIntentPrivate extends Showcase{

	private static String USERNAME = "user";
	private static String TOPIC = "/test2";

	public static void main(String[] args) throws Exception {
		client1 = initializeInternalMqttClient(USERNAME);
		client1.subscribe(TOPIC);
		client1.subscribe(ManagedZone.INFO.getTopicPrefix()+TOPIC+"/#");
		
		String inputJsonGlobal = ReadFileUtil.readFileString("/fce/showcase_manage.json");
		String inputJsonPrivate = ReadFileUtil.readFileString("/fce/showcase_manage_private.json");
		
		client1.publish(ManagedZone.INTENT.getTopicPrefix()+"/test2", inputJsonGlobal.getBytes(), Showcase.FIRE_AND_FORGET, true);
		client1.publish(ManagedZone.INTENT.getTopicPrefix()+"/test2", inputJsonPrivate.getBytes(), Showcase.FIRE_AND_FORGET, true);
		
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
