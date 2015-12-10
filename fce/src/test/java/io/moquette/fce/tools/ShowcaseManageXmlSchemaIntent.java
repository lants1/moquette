package io.moquette.fce.tools;

import io.moquette.fce.common.ReadFileUtil;
import io.moquette.fce.model.common.ManagedZone;

public class ShowcaseManageXmlSchemaIntent extends Showcase{

	private static final String SCHEMA_TOPIC = "/xml/schema";
	private static final String TOPIC = "/test77";
	private static final String INFO_TOPIC = ManagedZone.INFO.getTopicPrefix()+TOPIC+"/#";
	private static String USERNAME = "user";

	public static void main(String[] args) throws Exception {
		client1 = initializeInternalMqttClient(USERNAME);
		client1.subscribe(INFO_TOPIC);
		client1.subscribe(TOPIC);
		
		String manageFile = ReadFileUtil.readFileString("/fce/showcase_manage_xml.json");
		byte[] schemaXml = ReadFileUtil.readFileBytes("/validation/xml/sampleSchema.xml");
		
		client1.publish(SCHEMA_TOPIC, schemaXml, 1, true);
		client1.publish(ManagedZone.INTENT.getTopicPrefix()+TOPIC, manageFile.getBytes(), Showcase.FIRE_AND_FORGET, true);
		
		Thread.sleep(2000);
		bookXmlGood();
		Thread.sleep(2000); // accepted
		bookXmlBad();
		Thread.sleep(2000); // rejected
		
		disconnectClients();
	}

	public static void bookXmlGood() throws Exception {
		String jsonGood = ReadFileUtil.readFileString("/validation/xml/valid.xml");
		System.out.println("send good msg");
		client1.publish(TOPIC, jsonGood.getBytes(), Showcase.FIRE_AND_FORGET, true);
	}

	public static void bookXmlBad() throws Exception {
		String jsonBad = ReadFileUtil.readFileString("/validation/xml/invalid.xml");
		System.out.println("send bad json");
		client1.publish(TOPIC, jsonBad.getBytes(), Showcase.FIRE_AND_FORGET, true);
	}
}
