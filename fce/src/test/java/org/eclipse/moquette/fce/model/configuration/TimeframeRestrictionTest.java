package org.eclipse.moquette.fce.model.configuration;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.eclipse.moquette.fce.model.quota.IQuotaState;
import org.junit.Test;

public class TimeframeRestrictionTest {

	@Test
	public void testTimeframeRestrictionValidation() {
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		cal.add(Calendar.HOUR, -1);
		Date hourMinusOne = cal.getTime();
		cal.add(Calendar.HOUR, 2);
		Date hourPlusOne = cal.getTime();
		
		// valid case
		IQuotaState state = mock(IQuotaState.class);
		when(state.isValid(null, null)).thenReturn(true);
		TimeframeRestriction quotaValid = new TimeframeRestriction(null, hourMinusOne, hourPlusOne, 0, 0, 0, null,null);
		assertTrue(quotaValid.isValid(null, null));
		
		// invalid case past
		cal.setTime(new Date());
		cal.add(Calendar.HOUR, -2);
		Date hourMinusTwo = cal.getTime();
		
		TimeframeRestriction quotaInvalidPast = new TimeframeRestriction(null, hourMinusTwo, hourMinusOne, 0, 0, 0, null, null);
		assertFalse(quotaInvalidPast.isValid(null, null));
		
		// invalid case future
		cal.setTime(new Date());
		cal.add(Calendar.HOUR, 2);
		Date hourPlusTwo = cal.getTime();
		
		TimeframeRestriction quotaInvalidFuture = new TimeframeRestriction(null, hourPlusOne, hourPlusTwo, 0, 0, 0, null, null);
		assertFalse(quotaInvalidFuture.isValid(null, null));
	}


}
