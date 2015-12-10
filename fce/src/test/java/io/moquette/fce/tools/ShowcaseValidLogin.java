package io.moquette.fce.tools;

public class ShowcaseValidLogin extends Showcase {

	private static String USERNAME = "user";

	public static void main(String[] args) throws Exception {
		loginAndPublishToTopic();
		
		disconnectClients();
	}

	public static void loginAndPublishToTopic() throws Exception {
		client1 = initializeInternalMqttClient(USERNAME);
		client1.subscribe("/test1");
		client1.publish("/test1", "test".getBytes(), Showcase.FIRE_AND_FORGET, false);
	}

}
