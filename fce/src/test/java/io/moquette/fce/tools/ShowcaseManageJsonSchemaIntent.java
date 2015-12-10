package io.moquette.fce.tools;

import io.moquette.fce.common.ReadFileUtil;
import io.moquette.fce.model.common.ManagedZone;

public class ShowcaseManageJsonSchemaIntent extends Showcase{

	private static final String SCHEMA_TOPIC = "/json/schema";
	private static final String TOPIC = "/test33";
	private static final String INFO_TOPIC = ManagedZone.INFO.getTopicPrefix()+TOPIC+"/#";
	private static String USERNAME = "user";

	public static void main(String[] args) throws Exception {
		client1 = initializeInternalMqttClient(USERNAME);
		
		client1.subscribe(INFO_TOPIC);
		client1.subscribe(TOPIC);
		
		String manageFile = ReadFileUtil.readFileString("/fce/showcase_manage_json.json");
		String schemaJson = ReadFileUtil.readFileString("/validation/json/fstab.json");
		
		client1.publish(SCHEMA_TOPIC, schemaJson.getBytes(), 1, true);
		client1.publish(ManagedZone.INTENT.getTopicPrefix()+TOPIC, manageFile.getBytes(), Showcase.FIRE_AND_FORGET, true);
		
		Thread.sleep(2000);
		bookJsonGood();
		Thread.sleep(2000);
		bookJsonBad();
		Thread.sleep(2000); // quota depleted
		
		disconnectClients();
	}

	public static void bookJsonGood() throws Exception {
		String jsonGood = ReadFileUtil.readFileString("/validation/json/fstab-good.json");
		System.out.println("send good msg");
		client1.publish(TOPIC, jsonGood.getBytes(), Showcase.FIRE_AND_FORGET, true);
	}
	

	public static void bookJsonBad() throws Exception {
		String jsonBad = ReadFileUtil.readFileString("/validation/json/fstab-bad.json");
		System.out.println("send bad json");
		client1.publish(TOPIC, jsonBad.getBytes(), Showcase.FIRE_AND_FORGET, true);
	}

}
