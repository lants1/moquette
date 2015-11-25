package org.eclipse.moquette.fce.common;

import org.eclipse.moquette.fce.exception.FceSystemException;
import org.eclipse.moquette.fce.model.ManagedScope;
import org.eclipse.moquette.fce.service.ConfigurationDbService;
import org.eclipse.moquette.fce.service.IFceServiceFactory;
import org.eclipse.moquette.fce.service.JsonParserService;
import org.eclipse.moquette.fce.service.MqttService;
import org.eclipse.moquette.fce.service.QuotaDbService;
import org.eclipse.moquette.plugin.BrokerOperator;
import org.mockito.Mockito;

public class FceServiceFactoryMockImpl implements IFceServiceFactory {

	private BrokerOperator brokerOperator;

	private MqttService dataStoreService;
	private JsonParserService jsonParserService;
	private QuotaDbService quotaDbServiceGlobal;
	private ConfigurationDbService configDbServiceGlobal;
	private QuotaDbService quotaDbServicePrivate;
	private ConfigurationDbService configDbServicePrivate;

	public FceServiceFactoryMockImpl(BrokerOperator brokerOperator, ConfigurationDbService configDbServiceGlobal,
			QuotaDbService quotaDbServiceGlobal, ConfigurationDbService configDbServicePrivate,
			QuotaDbService quotaDbServicePrivate) {
		super();
		this.brokerOperator = brokerOperator;
		this.configDbServiceGlobal = configDbServiceGlobal;
		this.quotaDbServiceGlobal = quotaDbServiceGlobal;
		this.configDbServicePrivate = configDbServicePrivate;
		this.quotaDbServicePrivate = quotaDbServicePrivate;
	}

	public MqttService getMqtt() {
		if (dataStoreService == null) {
			dataStoreService = Mockito.mock(MqttService.class);
		}
		return dataStoreService;
	}

	public JsonParserService getJsonParser() {
		if (jsonParserService == null) {
			jsonParserService = new JsonParserService();
		}
		return jsonParserService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.moquette.fce.service.FceServiceFactory#getQuotaDbService()
	 */
	@Override
	public QuotaDbService getQuotaDb(ManagedZone zone) {
		if (ManagedZone.QUOTA_GLOBAL.equals(zone)) {
			if (quotaDbServiceGlobal == null) {
				quotaDbServiceGlobal = new QuotaDbService(brokerOperator, zone);
			}
			return quotaDbServiceGlobal;
		} else if (ManagedZone.QUOTA_PRIVATE.equals(zone)) {
			if (quotaDbServicePrivate == null) {
				quotaDbServicePrivate = new QuotaDbService(brokerOperator, zone);
			}
			return quotaDbServicePrivate;
		}
		throw new FceSystemException("invalid quota db");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.moquette.fce.service.FceServiceFactory#getConfigDbService()
	 */
	@Override
	public ConfigurationDbService getConfigDb(ManagedZone zone) {
		if (ManagedZone.CONFIG_GLOBAL.equals(zone)) {
			if (configDbServiceGlobal == null) {
				configDbServiceGlobal = new ConfigurationDbService(brokerOperator, zone);
			}
			return configDbServiceGlobal;
		} else if (ManagedZone.CONFIG_PRIVATE.equals(zone)) {
			if (configDbServicePrivate == null) {
				configDbServicePrivate = new ConfigurationDbService(brokerOperator, zone);
			}
			return configDbServicePrivate;
		}
		throw new FceSystemException("invalid config db");
	}

	@Override
	public ConfigurationDbService getConfigDb(ManagedScope scope) {
		if (ManagedScope.GLOBAL.equals(scope)) {
			return getConfigDb(ManagedZone.CONFIG_GLOBAL);
		} else if (ManagedScope.PRIVATE.equals(scope)) {
			return getConfigDb(ManagedZone.CONFIG_PRIVATE);
		}
		throw new FceSystemException("invalid scope for config db");
	}

	@Override
	public QuotaDbService getQuotaDb(ManagedScope scope) {
		if (ManagedScope.GLOBAL.equals(scope)) {
			return getQuotaDb(ManagedZone.QUOTA_GLOBAL);
		} else if (ManagedScope.PRIVATE.equals(scope)) {
			return getQuotaDb(ManagedZone.QUOTA_PRIVATE);
		}
		throw new FceSystemException("invalid scope for quota db");
	}

	public boolean isInitialized() {
		return true;
	}

}
