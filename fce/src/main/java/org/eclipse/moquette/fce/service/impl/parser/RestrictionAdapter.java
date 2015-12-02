package org.eclipse.moquette.fce.service.impl.parser;

import java.lang.reflect.Type;

import org.eclipse.moquette.fce.model.configuration.Restriction;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * GSON RestrictionAdapter for serialization/deserialization.
 * 
 * @author lants1
 *
 */
public class RestrictionAdapter implements JsonSerializer<Restriction>, JsonDeserializer<Restriction> {
	@Override
	public JsonElement serialize(Restriction src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject result = new JsonObject();
		result.add("type", new JsonPrimitive(src.getClass().getSimpleName()));
		result.add("properties", context.serialize(src, src.getClass()));
		return result;
	}

	@Override
	public Restriction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();
		String type = jsonObject.get("type").getAsString();
		JsonElement element = jsonObject.get("properties");

		try {
			String thepackage = "org.eclipse.moquette.fce.model.configuration.";
			return context.deserialize(element, Class.forName(thepackage + type));
		} catch (ClassNotFoundException cnfe) {
			throw new JsonParseException("Unknown element type: " + type, cnfe);
		}
	}
}