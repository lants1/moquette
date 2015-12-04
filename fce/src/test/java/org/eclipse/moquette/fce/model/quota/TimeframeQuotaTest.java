package org.eclipse.moquette.fce.model.quota;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Test;

import io.moquette.fce.model.quota.IQuotaState;
import io.moquette.fce.model.quota.TimeframeQuota;

public class TimeframeQuotaTest {

	@Test
	public void testTimeframeQuotaValidation() {
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		cal.add(Calendar.HOUR, -1);
		Date hourMinusOne = cal.getTime();
		cal.add(Calendar.HOUR, 2);
		Date hourPlusOne = cal.getTime();
		
		// valid case
		IQuotaState state = mock(IQuotaState.class);
		when(state.isValid(null, null, null)).thenReturn(true);
		TimeframeQuota quotaValid = new TimeframeQuota(hourMinusOne,hourPlusOne,state);
		assertTrue(quotaValid.isValid(null, null, null));
		
		// invalid case past
		cal.setTime(new Date());
		cal.add(Calendar.HOUR, -2);
		Date hourMinusTwo = cal.getTime();
		
		TimeframeQuota quotaInvalidPast = new TimeframeQuota(hourMinusTwo,hourMinusOne,state);
		assertFalse(quotaInvalidPast.isValid(null, null, null));
		
		// invalid case future
		cal.setTime(new Date());
		cal.add(Calendar.HOUR, 2);
		Date hourPlusTwo = cal.getTime();
		
		TimeframeQuota quotaInvalidFuture = new TimeframeQuota(hourPlusOne,hourPlusTwo,state);
		assertFalse(quotaInvalidFuture.isValid(null, null, null));
	}

}
