package org.eclipse.moquette.fce.job;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.eclipse.moquette.fce.model.quota.PeriodicQuotaState;
import org.eclipse.moquette.fce.model.quota.Quota;
import org.eclipse.moquette.fce.model.quota.QuotaState;
import org.eclipse.moquette.fce.service.FceServiceFactory;
import org.eclipse.moquette.fce.model.ManagedCycle;

public class QuotaUpdater implements Runnable {

	private final static Logger log = Logger.getLogger(QuotaUpdater.class.getName());

	private FceServiceFactory services;

	public QuotaUpdater(FceServiceFactory services) {
		super();
		this.services = services;
	}

	@Override
	public void run() {
		updateQuota();
		log.info("JOB: completed succefully");
	}

	private void updateQuota() {

		for (Entry<String, Quota> entry : services.getQuotaDbService().getAll()) {
			String key = entry.getKey();
			Quota quotaIn = entry.getValue();
			List<QuotaState> statesOut = new ArrayList<>();

			for (QuotaState state : quotaIn.getQuotaState()) {
				if (state instanceof PeriodicQuotaState) {
					PeriodicQuotaState newPeriodicState = (PeriodicQuotaState) state;
					if (isCycleExpired(newPeriodicState.getCycle(), newPeriodicState.getLastManagedTimestamp())) {
						newPeriodicState.setCurrentQuotaBytes(0);
						newPeriodicState.setCurrentQuotaCount(0);
						newPeriodicState.setLastManagedTimestamp(new Date());
					}
					statesOut.add(newPeriodicState);
				} else {
					statesOut.add(state);
				}
			}

			Quota quotaOut = new Quota(quotaIn.getUsergroup(), statesOut);
			services.getQuotaDbService().put(key, quotaOut);
		}

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
