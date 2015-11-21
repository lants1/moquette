package org.eclipse.moquette.fce.common.converter;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.moquette.fce.model.configuration.PeriodicRestriction;
import org.eclipse.moquette.fce.model.configuration.Restriction;
import org.eclipse.moquette.fce.model.configuration.TimeframeRestriction;
import org.eclipse.moquette.fce.model.quota.MessageCountState;
import org.eclipse.moquette.fce.model.quota.PeriodicQuota;
import org.eclipse.moquette.fce.model.quota.Quota;
import org.eclipse.moquette.fce.model.quota.QuotaState;
import org.eclipse.moquette.fce.model.quota.TimeframeQuota;
import org.eclipse.moquette.fce.model.quota.TransmittedDataState;

public final class ModelConverter {

	public static List<Quota> convertRestrictionsToQuotas(List<Restriction> restrictions) {
		List<Quota> subscribeState = new ArrayList<>();
		for (Restriction restriction : restrictions) {
			List<QuotaState> quotaStates = new ArrayList<>();
			if (restriction.getMessageCount() > 0) {
				quotaStates.add(new MessageCountState(0, restriction.getMessageCount()));
			}

			if (restriction.getTotalMessageSize() > 0) {
				quotaStates.add(new TransmittedDataState(0, restriction.getTotalMessageSize()));
			}

			if (restriction instanceof PeriodicRestriction) {
				for (QuotaState state : quotaStates) {
					subscribeState.add(new PeriodicQuota(((PeriodicRestriction) restriction).getCyle(), state));
				}
			}

			if (restriction instanceof TimeframeRestriction) {
				for (QuotaState state : quotaStates) {
					subscribeState.add(new TimeframeQuota(((TimeframeRestriction) restriction).getFrom(),
							((TimeframeRestriction) restriction).getTo(), state));
				}
			}
		}
		return subscribeState;
	}
}
