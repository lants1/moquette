package org.eclipse.moquette.fce.service;

import java.util.Properties;

import org.eclipse.moquette.fce.event.MqttEventHandler;
import org.eclipse.moquette.plugin.BrokerOperator;

public class FceServiceFactory {
	
	Properties pluginConfig;
	BrokerOperator brokerOperator;
	
	MqttService dataStoreService;
	AuthorizationService authorizationService;
	JsonParserService jsonParserService;
	QuotaDbService quotaDbService;
	ConfigurationDbService configDbService;
	
	public FceServiceFactory(Properties config, BrokerOperator brokerOperator){
		this.pluginConfig = config;
		this.brokerOperator = brokerOperator;
	}
	
	
	public MqttService getMqttDataStore(){
		if(dataStoreService == null){
			dataStoreService = new MqttService(pluginConfig, new MqttEventHandler(this));
			dataStoreService.initializeDataStore();
		}
		return dataStoreService;
	}
	
	public AuthorizationService getAuthorization(){
		if(authorizationService == null){
			authorizationService = new AuthorizationService();
		}
		return authorizationService;
	}
	
	public JsonParserService getJsonParser(){
		if(jsonParserService == null){
			jsonParserService = new JsonParserService();
		}
		return jsonParserService;
	}

	public QuotaDbService getQuotaDbService() {
		if(quotaDbService == null){
			quotaDbService = new QuotaDbService();
		}
		return quotaDbService;
	}

	public ConfigurationDbService getConfigDbService() {
		if(configDbService == null){
			configDbService = new ConfigurationDbService();
		}
		return configDbService;
	}

}
