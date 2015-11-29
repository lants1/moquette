package org.eclipse.moquette.fce.common;

import org.junit.Test;

import static org.junit.Assert.*;


public class FceHashUtilTest {
	
	public static String USER1 = "user";
	public static String USER2 = "user2";
	
	public static String PW1 = "pw";
	public static String PW2 = "pw2";
	
	@Test
	public void testHashing() throws Exception {
		String pw1hash = FceHashUtil.getFceHash(USER1, PW1);
		String pw2hash = FceHashUtil.getFceHash(USER2, PW2);

		assertFalse(pw1hash.equalsIgnoreCase(pw2hash));

		String pw3hash = FceHashUtil.getFceHash(USER1, PW1);

		assertTrue(pw3hash.equalsIgnoreCase(pw1hash));
	}

}