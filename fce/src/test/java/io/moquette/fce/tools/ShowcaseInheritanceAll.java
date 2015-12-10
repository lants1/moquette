package io.moquette.fce.tools;

import io.moquette.fce.common.ReadFileUtil;
import io.moquette.fce.model.common.ManagedZone;

public class ShowcaseInheritanceAll extends Showcase {

	private static final String SUBTOPIC = "/test5/bla";
	private static final String TOPIC = "/test5";
	private static String USER1 = "1fdd324asf";
	private static String USER2 = "2b43dasfss";

	public static void main(String[] args) throws Exception {
		client1 = initializeInternalMqttClient(USER1);
		client1.subscribe(TOPIC);
		client1.subscribe(ManagedZone.INFO.getTopicPrefix()+TOPIC+"/#");
		client2 = initializeInternalMqttClient(USER2);
		client2.subscribe(TOPIC);
		client2.subscribe(ManagedZone.INFO.getTopicPrefix()+TOPIC+"/#");

		String inputJson = ReadFileUtil.readFileString("/fce/showcase_manage_all.json");

		System.out.println("send intent");
		client2.publish(ManagedZone.INTENT.getTopicPrefix() + TOPIC, inputJson.getBytes(), Showcase.FIRE_AND_FORGET,
				true);

		Thread.sleep(2000);
		client1.publish(SUBTOPIC, "test".getBytes(), Showcase.FIRE_AND_FORGET, true);
		Thread.sleep(2000);
		client1.publish(SUBTOPIC, "test".getBytes(), Showcase.FIRE_AND_FORGET, true);
		Thread.sleep(2000);
		client1.publish(SUBTOPIC, "test".getBytes(), Showcase.FIRE_AND_FORGET, true);
		Thread.sleep(2000);
		client1.publish(SUBTOPIC, "test".getBytes(), Showcase.FIRE_AND_FORGET, true); // quota depleted*/
		Thread.sleep(2000);
		client2.publish(SUBTOPIC, "test".getBytes(), Showcase.FIRE_AND_FORGET, true); // should work...*/*/

		disconnectClients();
	}
}
