package org.eclipse.moquette.fce.service;

import java.util.Properties;

import org.eclipse.moquette.fce.event.MqttEventHandler;
import org.eclipse.moquette.plugin.BrokerOperator;

public class FceServiceFactoryImpl implements FceServiceFactory {

	private Properties pluginConfig;
	private BrokerOperator brokerOperator;

	private MqttService dataStoreService;
	private AuthorizationService authorizationService;
	private JsonParserService jsonParserService;
	private QuotaDbService quotaDbService;
	private ConfigurationDbService configDbService;
	private XmlSchemaValidationService schemaValidationService;

	public FceServiceFactoryImpl(Properties config, BrokerOperator brokerOperator) {
		this.pluginConfig = config;
		this.brokerOperator = brokerOperator;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.moquette.fce.service.FceServiceFactory#getMqttService()
	 */
	@Override
	public MqttService getMqttService() {
		if (dataStoreService == null) {
			dataStoreService = new MqttService(pluginConfig, new MqttEventHandler(this));
			dataStoreService.initializeInternalMqttClient();
		}
		return dataStoreService;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.moquette.fce.service.FceServiceFactory#getAuthorizationService()
	 */
	@Override
	public AuthorizationService getAuthorizationService() {
		if (authorizationService == null) {
			authorizationService = new AuthorizationService();
		}
		return authorizationService;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.moquette.fce.service.FceServiceFactory#getJsonParser()
	 */
	@Override
	public JsonParserService getJsonParser() {
		if (jsonParserService == null) {
			jsonParserService = new JsonParserService();
		}
		return jsonParserService;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.moquette.fce.service.FceServiceFactory#getQuotaDbService()
	 */
	@Override
	public QuotaDbService getQuotaDbService() {
		if (quotaDbService == null) {
			quotaDbService = new QuotaDbService(brokerOperator);
		}
		return quotaDbService;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.moquette.fce.service.FceServiceFactory#getConfigDbService()
	 */
	@Override
	public ConfigurationDbService getConfigDbService() {
		if (configDbService == null) {
			configDbService = new ConfigurationDbService(brokerOperator);
		}
		return configDbService;
	}

	@Override
	public XmlSchemaValidationService getXmlSchemaValidationService() {
		if (schemaValidationService == null) {
			schemaValidationService = new XmlSchemaValidationService();
		}
		return schemaValidationService;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.moquette.fce.service.FceServiceFactory#isInitialized()
	 */
	@Override
	public boolean isInitialized() {
		return (getConfigDbService().isInitialized() && this.getQuotaDbService().isInitialized());
	}
}
