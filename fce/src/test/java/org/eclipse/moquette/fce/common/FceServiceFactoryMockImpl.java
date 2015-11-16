package org.eclipse.moquette.fce.common;

import java.util.Properties;

import org.eclipse.moquette.fce.event.MqttEventHandler;
import org.eclipse.moquette.fce.service.AuthorizationService;
import org.eclipse.moquette.fce.service.ConfigurationDbService;
import org.eclipse.moquette.fce.service.FceServiceFactory;
import org.eclipse.moquette.fce.service.JsonParserService;
import org.eclipse.moquette.fce.service.MqttService;
import org.eclipse.moquette.fce.service.QuotaDbService;
import org.eclipse.moquette.plugin.BrokerOperator;

public class FceServiceFactoryMockImpl implements FceServiceFactory {

	private Properties pluginConfig;
	private BrokerOperator brokerOperator;

	private MqttService dataStoreService;
	private AuthorizationService authorizationService;
	private JsonParserService jsonParserService;
	private QuotaDbService quotaDbService;
	private ConfigurationDbService configDbService;

	public FceServiceFactoryMockImpl(Properties pluginConfig, BrokerOperator brokerOperator,
			MqttService dataStoreService, AuthorizationService authorizationService,
			JsonParserService jsonParserService, QuotaDbService quotaDbService,
			ConfigurationDbService configDbService) {
		super();
		this.pluginConfig = pluginConfig;
		this.brokerOperator = brokerOperator;
		this.dataStoreService = dataStoreService;
		this.authorizationService = authorizationService;
		this.jsonParserService = jsonParserService;
		this.quotaDbService = quotaDbService;
		this.configDbService = configDbService;
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
