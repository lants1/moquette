package org.eclipse.moquette.fce.service.hash;

import org.junit.Test;

import static org.junit.Assert.*;

import org.eclipse.moquette.fce.service.FceServiceFactory;


public class HashServiceTest {
	
	public static String USER1 = "user";
	public static String USER2 = "user2";
	
	@Test
	public void testHashing() throws Exception {
		FceServiceFactory services = new FceServiceFactory(null, null);
		String usr1hash = services.getHashing().generateHash(USER1);
		String usr2hash = services.getHashing().generateHash(USER2);

		assertFalse(usr1hash.equalsIgnoreCase(usr2hash));

		String pw3hash = services.getHashing().generateHash(USER1);

		assertTrue(pw3hash.equalsIgnoreCase(usr1hash));
	}

}