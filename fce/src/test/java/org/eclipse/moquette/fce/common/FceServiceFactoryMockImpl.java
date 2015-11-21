package org.eclipse.moquette.fce.common;

import java.util.Properties;

import org.eclipse.moquette.fce.service.AuthorizationService;
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

	public MqttService getMqtt() {
		if (dataStoreService == null) {
			dataStoreService = Mockito.mock(MqttService.class);
		}
		return dataStoreService;
	}

	public AuthorizationService getAuthorization() {
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

	public QuotaDbService getQuotaDb() {
		if (quotaDbService == null) {
			quotaDbService = Mockito.mock(QuotaDbService.class);
		}
		return quotaDbService;
	}

	public ConfigurationDbService getConfigDb() {
		if (configDbService == null) {
			configDbService = new ConfigurationDbService(brokerOperator);
		}
		return configDbService;
	}

	public boolean isInitialized() {
		return (getConfigDb().isInitialized() && this.getQuotaDb().isInitialized());
	}

}
