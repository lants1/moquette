package org.eclipse.moquette.fce.common;

import org.eclipse.moquette.fce.model.info.InfoMessage;
import org.junit.Test;

import static org.junit.Assert.*;


public class FceHashUtilTest {
	
	@Test
	public void testHashing() throws Exception {
		InfoMessage infoMsg = new InfoMessage("user", "pw", null, null);
		InfoMessage infoMsg2 = new InfoMessage("user2", "pw2", null, null);
	
		String pw = FceHashUtil.getFceHash(infoMsg);
		String pw2 = FceHashUtil.getFceHash(infoMsg2);

		assertFalse(pw.equalsIgnoreCase(pw2));

		String pw3 = FceHashUtil.getFceHash(infoMsg);

		assertTrue(pw3.equalsIgnoreCase(pw));
		
		
		InfoMessage infoMsg3 = new InfoMessage("fceplugin", "samplepw", null, null);
		System.out.println(FceHashUtil.getFceHash(infoMsg3));
	}

}