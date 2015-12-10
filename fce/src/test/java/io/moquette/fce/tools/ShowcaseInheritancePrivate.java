package io.moquette.fce.tools;

import io.moquette.fce.common.ReadFileUtil;
import io.moquette.fce.model.common.ManagedZone;

public class ShowcaseInheritancePrivate extends Showcase{

	private static final String SUBTOPIC = "/test7/bla";
	private static final String TOPIC = "/test7";
	private static final String INFO_TOPIC = ManagedZone.INFO.getTopicPrefix()+TOPIC+"/#";
	private static final String INTENT_TOPIC = ManagedZone.INTENT.getTopicPrefix()+TOPIC;
	private static String USERNAME = "user";

	public static void main(String[] args) throws Exception {
		client1 = initializeInternalMqttClient(USERNAME);
		
		String inputJsonGlobal = ReadFileUtil.readFileString("/fce/showcase_manage.json");
		String inputJsonPrivate = ReadFileUtil.readFileString("/fce/showcase_manage_private.json");
		
		client1.subscribe(INFO_TOPIC);
		client1.subscribe("/#");
		
		Thread.sleep(2000);
		client1.publish(INTENT_TOPIC, inputJsonGlobal.getBytes(), Showcase.FIRE_AND_FORGET, true);
		Thread.sleep(2000);
		client1.publish(INTENT_TOPIC, inputJsonPrivate.getBytes(), Showcase.FIRE_AND_FORGET, true);
		
		Thread.sleep(2000);
		bookQuota(SUBTOPIC);
		Thread.sleep(2000);
		bookQuota(SUBTOPIC);
		Thread.sleep(2000);
		bookQuota(SUBTOPIC); // private quota depleted*/
		Thread.sleep(2000);
		bookQuota(TOPIC);  // private quota depleted*/
		Thread.sleep(2000);
		bookQuota(TOPIC); //  public quota depleted*/
		
		disconnectClients();
	}

	public static void bookQuota(String topic) throws Exception {
		System.out.println("send msg on topic:"+topic);
		client1.publish(topic, "test".getBytes(), Showcase.FIRE_AND_FORGET, true);
	}
}
