package org.eclipse.moquette.fce.service;

import org.eclipse.moquette.fce.common.ManagedZone;
import org.eclipse.moquette.fce.model.ManagedScope;

public interface IFceServiceFactory {

	MqttService getMqtt();

	JsonParserService getJsonParser();

	ConfigurationDbService getConfigDb(ManagedZone zone);
	
	ConfigurationDbService getConfigDb(ManagedScope scope);

	QuotaDbService getQuotaDb(ManagedZone zone);
	
	QuotaDbService getQuotaDb(ManagedScope scope);
	
	boolean isInitialized();

}