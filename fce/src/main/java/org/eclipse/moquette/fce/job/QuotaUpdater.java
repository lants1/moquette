package org.eclipse.moquette.fce.job;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.eclipse.moquette.fce.model.quota.PeriodicQuota;
import org.eclipse.moquette.fce.model.quota.UserQuotaData;
import org.eclipse.moquette.fce.model.quota.Quota;
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

		for (Entry<String, UserQuotaData> entry : services.getQuotaDbService().getAll()) {
			String key = entry.getKey();
			UserQuotaData quotaIn = entry.getValue();
			List<Quota> statesOut = new ArrayList<>();

			for (Quota state : quotaIn.getQuotas()) {
				if (state instanceof PeriodicQuota) {
					PeriodicQuota newPeriodicState = (PeriodicQuota) state;
					if (isCycleExpired(newPeriodicState.getCycle(), newPeriodicState.getLastManagedTimestamp())) {
						newPeriodicState.flush();
						newPeriodicState.setLastManagedTimestamp(new Date());
					}
					statesOut.add(newPeriodicState);
				} else {
					statesOut.add(state);
				}
			}

			UserQuotaData quotaOut = new UserQuotaData(quotaIn.getUserName(), quotaIn.getUserIdentifier(), statesOut);
			services.getQuotaDbService().put(key, quotaOut);
			services.getMqttService().publish(key, services.getJsonParser().serialize(quotaOut), true);
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
