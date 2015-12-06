package io.moquette.fce.common.converter;

import java.util.ArrayList;
import java.util.List;

import io.moquette.fce.model.configuration.PeriodicRestriction;
import io.moquette.fce.model.configuration.Restriction;
import io.moquette.fce.model.configuration.TimeframeRestriction;
import io.moquette.fce.model.configuration.UserConfiguration;
import io.moquette.fce.model.quota.IQuotaState;
import io.moquette.fce.model.quota.MessageCountState;
import io.moquette.fce.model.quota.PeriodicQuota;
import io.moquette.fce.model.quota.Quota;
import io.moquette.fce.model.quota.TimeframeQuota;
import io.moquette.fce.model.quota.TransmittedDataState;
import io.moquette.fce.model.quota.UserQuota;
import io.moquette.plugin.MqttAction;

/**
 * Converts UserConfiguration to QuotaModel.
 * 
 * @author lants1
 *
 */
public final class QuotaConverter {

	private QuotaConverter() {
	}

	public static UserQuota convertSubscribeConfiguration(UserConfiguration config) {
		List<Quota> subscribeState = convertRestrictions(config.getRestrictions(MqttAction.SUBSCRIBE));
		return new UserQuota(config.getAlias(), config.getUserHash(), MqttAction.SUBSCRIBE, subscribeState);
	}

	public static UserQuota convertPublishConfiguration(UserConfiguration config) {
		List<Quota> subscribeState = convertRestrictions(config.getRestrictions(MqttAction.PUBLISH));
		return new UserQuota(config.getAlias(), config.getUserHash(), MqttAction.PUBLISH, subscribeState);
	}

	public static List<Quota> convertRestrictions(List<Restriction> restrictions) {
		List<Quota> resultRestrictions = new ArrayList<>();
		if (restrictions != null && !restrictions.isEmpty()) {
			for (Restriction restriction : restrictions) {
				List<IQuotaState> quotaStates = new ArrayList<>();
				if (restriction.getMessageCount() != 0) {
					quotaStates.add(new MessageCountState(restriction.getMessageCount(), 0));
				}

				if (restriction.getTotalMessageSize() != 0) {
					quotaStates.add(new TransmittedDataState(
							restriction.getTotalMessageSize() * restriction.getDataUnit().getMultiplier(), 0));
				}

				if (restriction instanceof PeriodicRestriction) {
					for (IQuotaState state : quotaStates) {
						resultRestrictions.add(new PeriodicQuota(((PeriodicRestriction) restriction).getCyle(), state));
					}
				}

				if (restriction instanceof TimeframeRestriction) {
					for (IQuotaState state : quotaStates) {
						resultRestrictions.add(new TimeframeQuota(((TimeframeRestriction) restriction).getFrom(),
								((TimeframeRestriction) restriction).getTo(), state));
					}
				}
			}
		}
		return resultRestrictions;
	}
}
