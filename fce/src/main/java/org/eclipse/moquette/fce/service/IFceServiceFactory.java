package org.eclipse.moquette.fce.service;

public interface IFceServiceFactory {

	MqttService getMqtt();

	AuthorizationService getAuthorization();

	JsonParserService getJsonParser();

	QuotaDbService getQuotaDb();

	ConfigurationDbService getConfigDb();
	
	boolean isInitialized();

}