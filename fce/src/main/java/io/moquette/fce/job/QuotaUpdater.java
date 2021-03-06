package io.moquette.fce.job;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import io.moquette.fce.context.FceContext;
import io.moquette.fce.exception.FceAuthorizationException;
import io.moquette.fce.exception.FceSystemException;
import io.moquette.fce.model.common.ManagedCycle;
import io.moquette.fce.model.common.ManagedZone;
import io.moquette.fce.model.quota.PeriodicQuota;
import io.moquette.fce.model.quota.Quota;
import io.moquette.fce.model.quota.UserQuota;
import io.moquette.fce.service.FceServiceFactory;

/**
 * QuotaUpdater job which is triggered by s scheduled executor service.
 * The updater flushes periodic quotas if managed cycle expires...
 * 
 * @author lants1
 *
 */
public class QuotaUpdater implements Runnable {

	private static final Logger LOGGER = Logger.getLogger(QuotaUpdater.class.getName());

	private FceServiceFactory services;
	private FceContext context;

	public QuotaUpdater(FceContext context, FceServiceFactory services) {
		super();
		this.services = services;
		this.context = context;
	}

	@Override
	public void run() {
		try {
			updateQuota();
		} catch (FceAuthorizationException e) {
			throw new FceSystemException("periodic updating job has unexpected behaviour", e);
		}
		LOGGER.info("JOB: completed succefully");
	}

	private void updateQuota() throws FceAuthorizationException {

		updateQuotaZone(ManagedZone.QUOTA_GLOBAL);
		updateQuotaZone(ManagedZone.QUOTA_PRIVATE);

	}

	private void updateQuotaZone(ManagedZone zone) throws FceAuthorizationException {
		for (Entry<String, UserQuota> entry : context.getQuotaStore(zone).getAll()) {
			String key = entry.getKey();
			UserQuota quotaIn = entry.getValue();
			List<Quota> statesOut = new ArrayList<>();

			for (Quota state : quotaIn.getQuotas()) {
				if (state instanceof PeriodicQuota) {
					PeriodicQuota newPeriodicState = updatePeriodicState(state);
					statesOut.add(newPeriodicState);
				} else {
					statesOut.add(state);
				}
			}

			UserQuota quotaOut = new UserQuota(quotaIn.getAlias(), quotaIn.getUserHash(), quotaIn.getAction(), statesOut);
			context.getQuotaStore(zone).put(key, quotaOut, true);
			services.getMqtt().publish(key, services.getJsonParser().serialize(quotaOut));
		}
	}

	private PeriodicQuota updatePeriodicState(Quota state) {
		PeriodicQuota newPeriodicState = (PeriodicQuota) state;
		if (isCycleExpired(newPeriodicState.getCycle(), newPeriodicState.getLastManagedTimestamp())) {
			newPeriodicState.flush();
			newPeriodicState.setLastManagedTimestamp(new Date());
		}
		return newPeriodicState;
	}

	private boolean isCycleExpired(ManagedCycle cycle, Date lastManagedTimestamp) {
		Calendar now = Calendar.getInstance();
		Calendar past = new GregorianCalendar();
		past.setTime(lastManagedTimestamp);

		if (now.get(cycle.getCalendarReference()) != past.get(cycle.getCalendarReference())) {
			return true;
		}
		return false;
	}

}
