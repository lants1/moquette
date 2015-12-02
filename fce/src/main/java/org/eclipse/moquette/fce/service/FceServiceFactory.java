package org.eclipse.moquette.fce.service;

import org.eclipse.moquette.fce.context.FceContext;
import org.eclipse.moquette.fce.event.MqttEventHandler;
import org.eclipse.moquette.fce.model.common.ManagedZone;
import org.eclipse.moquette.fce.model.configuration.SchemaType;
import org.eclipse.moquette.fce.service.FceServiceFactory;
import org.eclipse.moquette.fce.service.impl.mqtt.MqttService;
import org.eclipse.moquette.fce.service.impl.parser.JsonParserService;
import org.eclipse.moquette.fce.service.impl.validation.ISchemaValidationService;
import org.eclipse.moquette.fce.service.impl.validation.JsonSchemaValidationService;
import org.eclipse.moquette.fce.service.impl.validation.XmlSchemaValidationService;

/**
 * ServiceFactory which provides access to every service in one class.
 * 
 * @author lants1
 *
 */
public class FceServiceFactory {

	private FceContext context;
	private MqttService dataStoreService;

	public FceServiceFactory(FceContext serviceContext) {
		this.context = serviceContext;
	}

	protected FceContext getContext() {
		return context;
	}

	public MqttService getMqtt() {
		if (dataStoreService == null) {
			dataStoreService = new MqttService(getContext().getPluginConfig(), new MqttEventHandler(getContext(), this));
			dataStoreService.initializeInternalMqttClient();
		}
		return dataStoreService;
	}


	public JsonParserService getJsonParser() {
		return new JsonParserService();
	}
	

	public boolean isInitialized() {
		return (getMqtt() != null && getContext().getConfigurationStore(ManagedZone.CONFIG_GLOBAL).isInitialized()
				&& getContext().getQuotaStore(ManagedZone.QUOTA_GLOBAL).isInitialized()
				&& getContext().getConfigurationStore(ManagedZone.CONFIG_GLOBAL).isInitialized()
				&& getContext().getQuotaStore(ManagedZone.QUOTA_PRIVATE).isInitialized() && getMqtt().isInitialized());
	}

	public ISchemaValidationService getSchemaValidationService(SchemaType type) {
		if(SchemaType.JSON_SCHEMA.equals(type)){
			return new JsonSchemaValidationService();
		}
		else{
			return new XmlSchemaValidationService();
		}
	}
}
