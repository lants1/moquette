package org.eclipse.moquette.fce.service;

public interface FceServiceFactory {

	MqttService getMqttService();

	AuthorizationService getAuthorizationService();

	JsonParserService getJsonParser();

	QuotaDbService getQuotaDbService();

	ConfigurationDbService getConfigDbService();

	XmlSchemaValidationService getXmlSchemaValidationService();
	
	boolean isInitialized();

}