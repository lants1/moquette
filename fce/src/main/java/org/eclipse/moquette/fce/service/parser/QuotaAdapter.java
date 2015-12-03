package org.eclipse.moquette.fce.service.parser;

import java.lang.reflect.Type;

import org.eclipse.moquette.fce.model.quota.Quota;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * GSON QuotaAdapter for serialization/deserialization.
 * 
 * @author lants1
 *
 */
public class QuotaAdapter implements JsonSerializer<Quota>, JsonDeserializer<Quota> {
	
	@Override
	public JsonElement serialize(Quota src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject result = new JsonObject();
		result.add("type", new JsonPrimitive(src.getClass().getSimpleName()));
		result.add("properties", context.serialize(src, src.getClass()));
		return result;
	}

	@Override
	public Quota deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		JsonObject jsonObject = json.getAsJsonObject();
		String type = jsonObject.get("type").getAsString();
		JsonElement element = jsonObject.get("properties");

		try {
			String thepackage = "org.eclipse.moquette.fce.model.quota.";
			return context.deserialize(element, Class.forName(thepackage + type));
		} catch (ClassNotFoundException cnfe) {
			throw new JsonParseException("Unknown element type: " + type, cnfe);
		}
	}
}