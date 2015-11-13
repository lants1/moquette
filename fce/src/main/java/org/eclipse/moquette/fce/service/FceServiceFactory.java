package org.eclipse.moquette.fce.service;

import java.util.Properties;

import org.eclipse.moquette.fce.event.MqttEventHandler;
import org.eclipse.moquette.plugin.BrokerOperator;

public class FceServiceFactory {
	
	Properties pluginConfig;
	BrokerOperator brokerOperator;
	
	MqttDataStoreService dataStoreService;
	FceAuthorizationService authorizationService;
	ManagedJsonParserService jsonParserService;
	
	public FceServiceFactory(Properties config, BrokerOperator brokerOperator){
		this.pluginConfig = config;
		this.brokerOperator = brokerOperator;
	}
	
	public MqttDataStoreService getMqttDataStore(){
		if(dataStoreService == null){
			dataStoreService = new MqttDataStoreService(pluginConfig, new MqttEventHandler(this));
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
	
	public ManagedJsonParserService getJsonParser(){
		if(jsonParserService == null){
			jsonParserService = new ManagedJsonParserService();
		}
		return jsonParserService;
	}
}
