package org.eclipse.moquette.fce.common.util;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Test;

public class FceTimeUtilTest {

	@Test
	public void testDelayTo() {
		Calendar cal = new GregorianCalendar();
		int minute = cal.get(Calendar.MINUTE);
		int sec = cal.get(Calendar.SECOND);
		long delay = 0;
		if (minute < 59) {
			delay = FceTimeUtil.delayTo((minute + 1), sec);
		} else {
			delay = FceTimeUtil.delayTo(0, sec);
		}
		assertTrue(delay > 55 && delay < 65);
	}

}
