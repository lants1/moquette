package org.eclipse.moquette.fce.common;

import org.eclipse.moquette.fce.model.info.InfoMessage;
import org.junit.Test;

import static org.junit.Assert.*;


public class FceHashingTest {
	
	@Test
	public void testHashing() throws Exception {
		InfoMessage infoMsg = new InfoMessage("user", "pw", null, null);
		InfoMessage infoMsg2 = new InfoMessage("user2", "pw2", null, null);
	
		String pw = FceHashUtil.getHash(infoMsg);
		String pw2 = FceHashUtil.getHash(infoMsg2);

		assertFalse(pw.equalsIgnoreCase(pw2));

		String pw3 = FceHashUtil.getHash(infoMsg);

		assertTrue(pw3.equalsIgnoreCase(pw));
	}
}