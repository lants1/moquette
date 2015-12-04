package io.moquette.fce.job;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

import org.junit.Test;

import io.moquette.fce.context.FceContext;
import io.moquette.fce.context.QuotaStore;
import io.moquette.fce.exception.FceAuthorizationException;
import io.moquette.fce.job.QuotaUpdater;
import io.moquette.fce.model.common.ManagedCycle;
import io.moquette.fce.model.common.ManagedZone;
import io.moquette.fce.model.quota.PeriodicQuota;
import io.moquette.fce.model.quota.Quota;
import io.moquette.fce.model.quota.TimeframeQuota;
import io.moquette.fce.model.quota.TransmittedDataState;
import io.moquette.fce.model.quota.UserQuota;
import io.moquette.fce.service.FceServiceFactory;
import io.moquette.fce.service.mqtt.FceMqttClientWrapper;
import io.moquette.fce.service.parser.JsonParserService;
import io.moquette.plugin.MqttAction;

public class QuotaUpdaterTest {

	private static final String TESTIDENTIFIER = "testidentifier";
	private static final String TESTUSER = "testuser";
	private final String TEST_TOPIC1 = "/test1";
	private final String TEST_TOPIC2 = "/test2";

	@Test
	public void testHourly() throws FceAuthorizationException {
		Calendar lastHour = Calendar.getInstance();
		lastHour.add(Calendar.HOUR, -1);

		QuotaStore quotaService = new QuotaStore(null, ManagedZone.QUOTA_GLOBAL);
		List<Quota> quotaStates = new ArrayList<>();

		PeriodicQuota hourlyState1 = new PeriodicQuota(ManagedCycle.HOURLY,
				new TransmittedDataState(2, 2));
		hourlyState1.setLastManagedTimestamp(lastHour.getTime());

		PeriodicQuota hourlyState2 = new PeriodicQuota(ManagedCycle.WEEKLY,
				new TransmittedDataState(2, 2));
		hourlyState2.setLastManagedTimestamp(lastHour.getTime());

		TimeframeQuota specState = new TimeframeQuota(new Date(), new Date(),
				new TransmittedDataState(2, 2));

		quotaStates.add(specState);
		quotaStates.add(hourlyState1);
		quotaStates.add(hourlyState2);

		UserQuota quota = new UserQuota(TESTUSER, TESTIDENTIFIER, MqttAction.SUBSCRIBE, quotaStates);
		quotaService.put(TEST_TOPIC1, quota, true);
		quotaService.put(TEST_TOPIC2, quota, true);
		
		FceServiceFactory serviceFactoryMock = mock(FceServiceFactory.class);
		FceContext contextMock = mock(FceContext.class);
		
		when(contextMock.getQuotaStore(any(ManagedZone.class))).thenReturn(quotaService);
		when(serviceFactoryMock.getMqtt()).thenReturn(mock(FceMqttClientWrapper.class));
		when(serviceFactoryMock.getJsonParser()).thenReturn(new JsonParserService());
		QuotaUpdater updater = new QuotaUpdater(contextMock, serviceFactoryMock);
		updater.run();

		UserQuota result1 = contextMock.getQuotaStore(ManagedZone.QUOTA_GLOBAL).get(TEST_TOPIC1);
		UserQuota result2 = contextMock.getQuotaStore(ManagedZone.QUOTA_GLOBAL).get(TEST_TOPIC2);

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
