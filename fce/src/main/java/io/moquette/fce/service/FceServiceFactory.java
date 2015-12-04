package io.moquette.fce.service;

import java.nio.ByteBuffer;

import io.moquette.fce.context.FceContext;
import io.moquette.fce.model.configuration.DataSchema;
import io.moquette.fce.model.configuration.SchemaType;
import io.moquette.fce.service.FceServiceFactory;
import io.moquette.fce.service.hash.HashService;
import io.moquette.fce.service.mqtt.MqttService;
import io.moquette.fce.service.parser.JsonParserService;
import io.moquette.fce.service.validation.ISchemaValidationService;
import io.moquette.fce.service.validation.JsonSchemaValidationService;
import io.moquette.fce.service.validation.XmlSchemaValidationService;

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
