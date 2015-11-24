package org.eclipse.moquette.fce.common.converter;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.moquette.fce.model.configuration.PeriodicRestriction;
import org.eclipse.moquette.fce.model.configuration.Restriction;
import org.eclipse.moquette.fce.model.configuration.TimeframeRestriction;
import org.eclipse.moquette.fce.model.configuration.UserConfiguration;
import org.eclipse.moquette.fce.model.quota.MessageCountState;
import org.eclipse.moquette.fce.model.quota.PeriodicQuota;
import org.eclipse.moquette.fce.model.quota.Quota;
import org.eclipse.moquette.fce.model.quota.IQuotaState;
import org.eclipse.moquette.fce.model.quota.TimeframeQuota;
import org.eclipse.moquette.fce.model.quota.TransmittedDataState;
import org.eclipse.moquette.fce.model.quota.UserQuota;
import org.eclipse.moquette.plugin.MqttAction;

public final class QuotaConverter {

	public static UserQuota convertSubscribeConfiguration(UserConfiguration config) {
		List<Quota> subscribeState = convertRestrictions(config.getRestrictions(MqttAction.SUBSCRIBE));
		return new UserQuota(config.getUserName(), config.getUserIdentifier(), subscribeState);
	}
	
	public static UserQuota convertPublishConfiguration(UserConfiguration config) {
		List<Quota> subscribeState = convertRestrictions(config.getRestrictions(MqttAction.PUBLISH));
		return new UserQuota(config.getUserName(), config.getUserIdentifier(), subscribeState);
	}
	
	public static List<Quota> convertRestrictions(List<Restriction> restrictions) {
		List<Quota> subscribeState = new ArrayList<>();
		for (Restriction restriction : restrictions) {
			List<IQuotaState> quotaStates = new ArrayList<>();
			if (restriction.getMessageCount() > 0) {
				quotaStates.add(new MessageCountState(0, restriction.getMessageCount()));
			}

			if (restriction.getTotalMessageSize() > 0) {
				quotaStates.add(new TransmittedDataState(0, restriction.getTotalMessageSize() * restriction.getDataUnit().getMultiplier()));
			}

			if (restriction instanceof PeriodicRestriction) {
				for (IQuotaState state : quotaStates) {
					subscribeState.add(new PeriodicQuota(((PeriodicRestriction) restriction).getCyle(), state));
				}
			}

			if (restriction instanceof TimeframeRestriction) {
				for (IQuotaState state : quotaStates) {
					subscribeState.add(new TimeframeQuota(((TimeframeRestriction) restriction).getFrom(),
							((TimeframeRestriction) restriction).getTo(), state));
				}
			}
		}
		return subscribeState;
	}
}
