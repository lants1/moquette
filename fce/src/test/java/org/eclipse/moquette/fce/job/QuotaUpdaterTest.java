package org.eclipse.moquette.fce.job;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.eclipse.moquette.fce.common.FceServiceFactoryMockImpl;
import org.eclipse.moquette.fce.common.SizeUnit;
import org.eclipse.moquette.fce.model.ManagedCycle;
import org.eclipse.moquette.fce.model.quota.PeriodicQuotaState;
import org.eclipse.moquette.fce.model.quota.Quota;
import org.eclipse.moquette.fce.model.quota.QuotaState;
import org.eclipse.moquette.fce.model.quota.SpecificQuotaState;
import org.eclipse.moquette.fce.service.QuotaDbService;
import org.junit.Test;

public class QuotaUpdaterTest {

	private static final String TESTIDENTIFIER = "testidentifier";
	private static final String TESTUSER = "testuser";
	private final String TEST_TOPIC1 = "/test1";
	private final String TEST_TOPIC2 = "/test2";

	@Test
	public void testHourly() {
		Calendar lastHour = Calendar.getInstance();
		lastHour.add(Calendar.HOUR, -1);

		QuotaDbService quotaService = new QuotaDbService(null);
		List<QuotaState> quotaStates = new ArrayList<>();

		PeriodicQuotaState hourlyState1 = new PeriodicQuotaState(ManagedCycle.HOURLY, 2, 2, 2, 2, SizeUnit.kB);
		hourlyState1.setLastManagedTimestamp(lastHour.getTime());

		PeriodicQuotaState hourlyState2 = new PeriodicQuotaState(ManagedCycle.WEEKLY, 2, 2, 2, 2, SizeUnit.kB);
		hourlyState2.setLastManagedTimestamp(lastHour.getTime());

		SpecificQuotaState specState = new SpecificQuotaState(new Date(), new Date(), 2, 2, 2, 2, SizeUnit.kB);

		quotaStates.add(specState);
		quotaStates.add(hourlyState1);
		quotaStates.add(hourlyState2);

		Quota quota = new Quota(TESTUSER, TESTIDENTIFIER, quotaStates);
		quotaService.put(TEST_TOPIC1, quota);
		quotaService.put(TEST_TOPIC2, quota);
		FceServiceFactoryMockImpl serviceFactoryMock = new FceServiceFactoryMockImpl(null, null, null, null, null,
				quotaService, null);
		QuotaUpdater updater = new QuotaUpdater(serviceFactoryMock);
		updater.run();

		Quota result1 = serviceFactoryMock.getQuotaDbService().get(TEST_TOPIC1);
		Quota result2 = serviceFactoryMock.getQuotaDbService().get(TEST_TOPIC1);
		
		assertTrue(result1.getQuotaState().size() == 3);
		assertTrue(result2.getQuotaState().size() == 3);

		for (QuotaState resultState : result1.getQuotaState()) {
			if (resultState instanceof PeriodicQuotaState) {
				PeriodicQuotaState perResultState = (PeriodicQuotaState) resultState;
				if (perResultState.getCycle() == ManagedCycle.WEEKLY) {
					assertTrue(perResultState.getCurrentQuotaBytes() == 2);
					assertTrue(perResultState.getCurrentQuotaCount() == 2);
				} else {
					assertTrue(perResultState.getCycle() == ManagedCycle.HOURLY);
					assertTrue(perResultState.getCurrentQuotaBytes() == 0);
					assertTrue(perResultState.getCurrentQuotaCount() == 0);

				}
			}
		}
	}

}
