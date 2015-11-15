package org.eclipse.moquette.fce.service;

import java.util.Properties;

import org.eclipse.moquette.fce.event.MqttEventHandler;
import org.eclipse.moquette.plugin.BrokerOperator;

public class FceServiceFactory {

	private Properties pluginConfig;
	private BrokerOperator brokerOperator;

	private MqttService dataStoreService;
	private AuthorizationService authorizationService;
	private JsonParserService jsonParserService;
	private QuotaDbService quotaDbService;
	private ConfigurationDbService configDbService;

	public FceServiceFactory(Properties config, BrokerOperator brokerOperator) {
		this.pluginConfig = config;
		this.brokerOperator = brokerOperator;
	}

	public MqttService getMqttService() {
		if (dataStoreService == null) {
			dataStoreService = new MqttService(pluginConfig, new MqttEventHandler(this));
			dataStoreService.initializeInternalMqttClient();
		}
		return dataStoreService;
	}

	public AuthorizationService getAuthorizationService() {
		if (authorizationService == null) {
			authorizationService = new AuthorizationService();
		}
		return authorizationService;
	}

	public JsonParserService getJsonParser() {
		if (jsonParserService == null) {
			jsonParserService = new JsonParserService();
		}
		return jsonParserService;
	}

	public QuotaDbService getQuotaDbService() {
		if (quotaDbService == null) {
			quotaDbService = new QuotaDbService(brokerOperator);
		}
		return quotaDbService;
	}

	public ConfigurationDbService getConfigDbService() {
		if (configDbService == null) {
			configDbService = new ConfigurationDbService(brokerOperator);
		}
		return configDbService;
	}

	public boolean isInitialized() {
		return (getConfigDbService().isInitialized() && this.getQuotaDbService().isInitialized());
	}
}
