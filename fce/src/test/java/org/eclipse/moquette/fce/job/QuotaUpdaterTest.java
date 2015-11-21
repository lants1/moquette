package org.eclipse.moquette.fce.job;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.eclipse.moquette.fce.common.FceServiceFactoryMockImpl;
import org.eclipse.moquette.fce.common.SizeUnit;
import org.eclipse.moquette.fce.model.ManagedCycle;
import org.eclipse.moquette.fce.model.quota.PeriodicQuota;
import org.eclipse.moquette.fce.model.quota.UserQuotaData;
import org.eclipse.moquette.fce.model.quota.Quota;
import org.eclipse.moquette.fce.model.quota.TimeframeQuota;
import org.eclipse.moquette.fce.model.quota.TransmittedDataState;
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
		List<Quota> quotaStates = new ArrayList<>();

		PeriodicQuota hourlyState1 = new PeriodicQuota(ManagedCycle.HOURLY,
				new TransmittedDataState(2, 2, SizeUnit.B));
		hourlyState1.setLastManagedTimestamp(lastHour.getTime());

		PeriodicQuota hourlyState2 = new PeriodicQuota(ManagedCycle.WEEKLY,
				new TransmittedDataState(2, 2, SizeUnit.B));
		hourlyState2.setLastManagedTimestamp(lastHour.getTime());

		TimeframeQuota specState = new TimeframeQuota(new Date(), new Date(),
				new TransmittedDataState(2, 2, SizeUnit.B));

		quotaStates.add(specState);
		quotaStates.add(hourlyState1);
		quotaStates.add(hourlyState2);

		UserQuotaData quota = new UserQuotaData(TESTUSER, TESTIDENTIFIER, quotaStates);
		quotaService.put(TEST_TOPIC1, quota);
		quotaService.put(TEST_TOPIC2, quota);
		FceServiceFactoryMockImpl serviceFactoryMock = new FceServiceFactoryMockImpl(null, null, null, quotaService);
		QuotaUpdater updater = new QuotaUpdater(serviceFactoryMock);
		updater.run();

		UserQuotaData result1 = serviceFactoryMock.getQuotaDbService().get(TEST_TOPIC1);
		UserQuotaData result2 = serviceFactoryMock.getQuotaDbService().get(TEST_TOPIC2);

		assertTrue(result1.getQuotas().size() == 3);
		assertTrue(result2.getQuotas().size() == 3);

		for (Quota resultState : result1.getQuotas()) {
			if (resultState instanceof PeriodicQuota) {
				PeriodicQuota perResultState = (PeriodicQuota) resultState;
				if (perResultState.getCycle() == ManagedCycle.WEEKLY) {
					assertTrue(((TransmittedDataState) perResultState.getState()).getCurrentQuota() == 2);
				} else {
					assertTrue(perResultState.getCycle() == ManagedCycle.HOURLY);
					assertTrue(((TransmittedDataState) perResultState.getState()).getCurrentQuota() == 0);
				}
			}
		}
	}

}
