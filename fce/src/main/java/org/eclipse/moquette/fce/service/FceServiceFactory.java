package org.eclipse.moquette.fce.service;

import java.util.Properties;

import org.eclipse.moquette.fce.event.MqttEventHandler;
import org.eclipse.moquette.plugin.BrokerOperator;

public class FceServiceFactory {
	
	Properties pluginConfig;
	BrokerOperator brokerOperator;
	
	MqttService dataStoreService;
	FceAuthorizationService authorizationService;
	FceJsonParserService jsonParserService;
	
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
	
	public FceAuthorizationService getAuthorization(){
		if(authorizationService == null){
			authorizationService = new FceAuthorizationService();
		}
		return authorizationService;
	}
	
	public FceJsonParserService getJsonParser(){
		if(jsonParserService == null){
			jsonParserService = new FceJsonParserService();
		}
		return jsonParserService;
	}
}
