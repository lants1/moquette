package org.eclipse.moquette.fce.service;

import org.eclipse.moquette.fce.FcePlugin;
import org.eclipse.moquette.fce.event.MqttEventHandler;
import org.eclipse.moquette.fce.exception.FceSystemException;
import org.eclipse.moquette.fce.model.common.ManagedScope;
import org.eclipse.moquette.fce.model.common.ManagedZone;
import org.eclipse.moquette.plugin.IBrokerConfig;
import org.eclipse.moquette.plugin.IBrokerOperator;

/**
 * ServiceFactory which provides access to every service in one class.
 * 
 * @author lants1
 *
 */
public class FceServiceFactoryImpl implements IFceServiceFactory {

	private IBrokerConfig pluginConfig;
	private IBrokerOperator brokerOperator;

	private MqttService dataStoreService;
	private JsonParserService jsonParserService;
	private QuotaDbService quotaDbServiceGlobal;
	private QuotaDbService quotaDbServicePrivate;
	private ConfigurationDbService configDbServiceGlobal;
	private ConfigurationDbService configDbServicePrivate;

	public FceServiceFactoryImpl(IBrokerConfig config, IBrokerOperator brokerOperator) {
		this.pluginConfig = config;
		this.brokerOperator = brokerOperator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.moquette.fce.service.FceServiceFactory#getMqttService()
	 */
	@Override
	public MqttService getMqtt() {
		if (dataStoreService == null) {
			dataStoreService = new MqttService(pluginConfig, new MqttEventHandler(this, pluginConfig.getProperty(FcePlugin.PROPS_PLUGIN_CLIENT_IDENTIFIER)));
			dataStoreService.initializeInternalMqttClient();
		}
		return dataStoreService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.moquette.fce.service.FceServiceFactory#getJsonParser()
	 */
	@Override
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.moquette.fce.service.FceServiceFactory#isInitialized()
	 */
	@Override
	public boolean isInitialized() {
		return (getMqtt() != null && getConfigDb(ManagedZone.CONFIG_GLOBAL).isInitialized()
				&& getQuotaDb(ManagedZone.QUOTA_GLOBAL).isInitialized()
				&& getConfigDb(ManagedZone.CONFIG_GLOBAL).isInitialized()
				&& getQuotaDb(ManagedZone.QUOTA_PRIVATE).isInitialized() && getMqtt().isInitialized());
	}
}
