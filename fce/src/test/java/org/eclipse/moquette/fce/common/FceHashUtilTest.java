package org.eclipse.moquette.fce.common;

import org.eclipse.moquette.fce.model.info.InfoMessage;
import org.junit.Test;

import static org.junit.Assert.*;


public class FceHashUtilTest {
	
	@Test
	public void testHashing() throws Exception {
		InfoMessage infoMsg = new InfoMessage("user", "pw", null, null);
		InfoMessage infoMsg2 = new InfoMessage("user2", "pw2", null, null);
	
		String pw = FceHashUtil.getFceHashWithPepper(infoMsg);
		String pw2 = FceHashUtil.getFceHashWithPepper(infoMsg2);

		assertFalse(pw.equalsIgnoreCase(pw2));

		String pw3 = FceHashUtil.getFceHashWithPepper(infoMsg);

		assertTrue(pw3.equalsIgnoreCase(pw));
		
		
		InfoMessage infoMsg3 = new InfoMessage("fceplugin", "samplepw", null, null);
		System.out.println(FceHashUtil.getFceHashWithPepper(infoMsg3));
	}

}