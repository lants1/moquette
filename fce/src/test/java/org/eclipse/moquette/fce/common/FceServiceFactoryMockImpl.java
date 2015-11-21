package org.eclipse.moquette.fce.common;

import java.util.Properties;

import org.eclipse.moquette.fce.service.AuthorizationService;
import org.eclipse.moquette.fce.service.ConfigurationDbService;
import org.eclipse.moquette.fce.service.FceServiceFactory;
import org.eclipse.moquette.fce.service.JsonParserService;
import org.eclipse.moquette.fce.service.MqttService;
import org.eclipse.moquette.fce.service.QuotaDbService;
import org.eclipse.moquette.plugin.BrokerOperator;
import org.mockito.Mockito;

public class FceServiceFactoryMockImpl implements FceServiceFactory {

	private BrokerOperator brokerOperator;

	private MqttService dataStoreService;
	private AuthorizationService authorizationService;
	private JsonParserService jsonParserService;
	private QuotaDbService quotaDbService;
	private ConfigurationDbService configDbService;

	public FceServiceFactoryMockImpl(Properties pluginConfig, BrokerOperator brokerOperator,
			ConfigurationDbService configDbService, QuotaDbService quotaDbService) {
		super();
		this.brokerOperator = brokerOperator;
		this.configDbService = configDbService;
		this.quotaDbService = quotaDbService;
	}

	public MqttService getMqttService() {
		if (dataStoreService == null) {
			dataStoreService = Mockito.mock(MqttService.class);
		}
		return dataStoreService;
	}

	public AuthorizationService getAuthorizationService() {
		if (authorizationService == null) {
			authorizationService = Mockito.mock(AuthorizationService.class);
		}
		return authorizationService;
	}

	public JsonParserService getJsonParser() {
		if (jsonParserService == null) {
			jsonParserService = Mockito.mock(JsonParserService.class);
		}
		return jsonParserService;
	}

	public QuotaDbService getQuotaDbService() {
		if (quotaDbService == null) {
			quotaDbService = Mockito.mock(QuotaDbService.class);
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
