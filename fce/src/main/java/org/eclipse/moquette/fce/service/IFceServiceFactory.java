package org.eclipse.moquette.fce.service;

import org.eclipse.moquette.fce.model.common.ManagedScope;
import org.eclipse.moquette.fce.model.common.ManagedZone;

/**
 * Interface for the FceServiceFactory...
 * 
 * @author lants1
 *
 */
public interface IFceServiceFactory {

	MqttService getMqtt();

	JsonParserService getJsonParser();

	ConfigurationDbService getConfigDb(ManagedZone zone);
	
	ConfigurationDbService getConfigDb(ManagedScope scope);

	QuotaDbService getQuotaDb(ManagedZone zone);
	
	QuotaDbService getQuotaDb(ManagedScope scope);
	
	HashClientIdAssignmentService getHashAssignmentService();
	
	boolean isInitialized();

}