package io.moquette.fce.tools;

import org.eclipse.paho.client.mqttv3.MqttClient;

import io.moquette.fce.common.ReadFileUtil;
import io.moquette.fce.model.common.ManagedZone;

public class ShowcaseManageIntentAll extends Showcase{

	private static String USER1 = "1fdd324asf";
	private static String USER2 = "2b43dasfss";
	
	private static String TOPIC = "/test56";
	private static String INTENT_TOPIC = ManagedZone.INTENT.getTopicPrefix()+TOPIC;
	private static String INFO_TOPIC = ManagedZone.INFO.getTopicPrefix()+TOPIC+"/#";

	public static void main(String[] args) throws Exception {
		client1 = initializeInternalMqttClient(USER2);
		client1.subscribe(INFO_TOPIC);
		client1.subscribe(TOPIC);
		client2 = initializeInternalMqttClient(USER1);
		client2.subscribe(INFO_TOPIC);
		client2.subscribe(TOPIC);
		
		String inputJson = ReadFileUtil.readFileString("/fce/showcase_manage_all.json");
		client2.publish(INTENT_TOPIC, inputJson.getBytes(), Showcase.FIRE_AND_FORGET, true);
		
		Thread.sleep(2000);
		bookQuota(client2);
		Thread.sleep(2000);
		bookQuota(client2);
		Thread.sleep(2000);
		bookQuota(client2);
		Thread.sleep(2000);
		bookQuota(client2); // quota depleted*/
		Thread.sleep(2000);
		bookQuota(client1); // should work...*/*/
		
		disconnectClients();
	}

	public static void bookQuota(MqttClient client) throws Exception {
		client.publish(TOPIC, "test".getBytes(), Showcase.FIRE_AND_FORGET, true);
	}
}
