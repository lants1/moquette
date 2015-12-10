package io.moquette.fce.tools;

import io.moquette.fce.model.common.ManagedZone;

public class ShowcaseManageStore extends Showcase{

	private static String USERNAME = "user";

	public static void main(String[] args) throws Exception {
		// should not work
		publishToManageStore();
	}

	public static void publishToManageStore() throws Exception {
		client1 = initializeInternalMqttClient(USERNAME);

		client1.publish(ManagedZone.CONFIG_GLOBAL.getTopicPrefix()+"/test1", "asfd".getBytes(), Showcase.FIRE_AND_FORGET, true);
	
		disconnectClients();
	}
}
