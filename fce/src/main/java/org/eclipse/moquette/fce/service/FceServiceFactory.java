package org.eclipse.moquette.fce.service;

import java.nio.ByteBuffer;

import org.eclipse.moquette.fce.context.FceContext;
import org.eclipse.moquette.fce.model.configuration.DataSchema;
import org.eclipse.moquette.fce.model.configuration.SchemaType;
import org.eclipse.moquette.fce.service.FceServiceFactory;
import org.eclipse.moquette.fce.service.hash.HashService;
import org.eclipse.moquette.fce.service.mqtt.MqttService;
import org.eclipse.moquette.fce.service.parser.JsonParserService;
import org.eclipse.moquette.fce.service.validation.ISchemaValidationService;
import org.eclipse.moquette.fce.service.validation.JsonSchemaValidationService;
import org.eclipse.moquette.fce.service.validation.XmlSchemaValidationService;

/**
 * ServiceFactory which provides access to every service in one class.
 * 
 * @author lants1
 *
 */
public class FceServiceFactory {

	private FceContext context;
	private MqttService mqttService;

	public FceServiceFactory(FceContext serviceContext, MqttService mqttService) {
		this.context = serviceContext;
		this.mqttService = mqttService;
	}

	private FceContext getContext() {
		return context;
	}
	
	public HashService getHashing() {
		return new HashService();
	}
	
	public JsonParserService getJsonParser() {
		return new JsonParserService();
	}

	public MqttService getMqtt() {
		return mqttService;
	}

	public ISchemaValidationService getSchemaValidationService(DataSchema dataSchema) {
		ByteBuffer schema = getContext().getSchemaAssignment().get(dataSchema.getSchemaTopic());
		if(SchemaType.JSON_SCHEMA.equals(dataSchema.getSchemaType())){
			return new JsonSchemaValidationService(schema);
		}
		else{
			return new XmlSchemaValidationService(schema);
		}
	}
}
