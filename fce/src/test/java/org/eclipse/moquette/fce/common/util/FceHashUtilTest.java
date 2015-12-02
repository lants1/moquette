package org.eclipse.moquette.fce.common.util;

import org.junit.Test;

import static org.junit.Assert.*;

import org.eclipse.moquette.fce.common.util.FceHashUtil;


public class FceHashUtilTest {
	
	public static String USER1 = "user";
	public static String USER2 = "user2";
	
	@Test
	public void testHashing() throws Exception {
		String usr1hash = FceHashUtil.getFceHash(USER1);
		String usr2hash = FceHashUtil.getFceHash(USER2);

		assertFalse(usr1hash.equalsIgnoreCase(usr2hash));

		String pw3hash = FceHashUtil.getFceHash(USER1);

		assertTrue(pw3hash.equalsIgnoreCase(usr1hash));
	}

}