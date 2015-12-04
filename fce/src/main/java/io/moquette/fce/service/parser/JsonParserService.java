package io.moquette.fce.service.parser;

import java.nio.ByteBuffer;

import com.google.gson.GsonBuilder;

import io.moquette.fce.exception.FceSystemException;
import io.moquette.fce.model.configuration.Restriction;
import io.moquette.fce.model.configuration.UserConfiguration;
import io.moquette.fce.model.info.InfoMessage;
import io.moquette.fce.model.quota.IQuotaState;
import io.moquette.fce.model.quota.Quota;
import io.moquette.fce.model.quota.UserQuota;

/**
 * Parsing Service from Model to Json / Json to Model serialization and deserialisation.
 * 
 * @author lants1
 *
 */
public class JsonParserService {

	public String serialize(UserConfiguration userConfig) {
		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(Restriction.class, new RestrictionAdapter());
		return gson.create().toJson(userConfig);
	}

	public String serialize(UserQuota quota) {
		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(Quota.class, new QuotaAdapter());
		gson.registerTypeAdapter(IQuotaState.class, new QuotaStateAdapter());
		return gson.create().toJson(quota);
	}

	public String serialize(InfoMessage infoMessage) {
		GsonBuilder gson = new GsonBuilder();
		return gson.create().toJson(infoMessage);
	}

	public UserConfiguration deserializeUserConfiguration(String userConfigString) {
		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(Restriction.class, new RestrictionAdapter());
		UserConfiguration userConfigObject = null;
		try {
			userConfigObject = gson.create().fromJson(userConfigString, UserConfiguration.class);
		} catch (Exception e) {
			// could happen because the UserConfiguration comes from the user
			// itself...
		}
		return userConfigObject;
	}

	public UserConfiguration deserializeUserConfiguration(ByteBuffer msg) {
		return deserializeUserConfiguration(convertToString(msg));
	}

	public UserQuota deserializeQuota(String quotaStateString) {
		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(Quota.class, new QuotaAdapter());
		gson.registerTypeAdapter(IQuotaState.class, new QuotaStateAdapter());
		UserQuota userConfigObject = null;
		try {
			userConfigObject = gson.create().fromJson(quotaStateString, UserQuota.class);
		} catch (Exception e) {
			// something evil is wrong, stop plugin
			throw new FceSystemException(e);
		}
		return userConfigObject;
	}

	public UserQuota deserializeQuota(ByteBuffer msg) {
		return deserializeQuota(convertToString(msg));
	}

	public InfoMessage deserializeInfoMessage(String infoMessageString) {
		GsonBuilder gson = new GsonBuilder();
		InfoMessage userConfigObject = null;
		try {
			userConfigObject = gson.create().fromJson(infoMessageString, InfoMessage.class);
		} catch (Exception e) {
			// something evil is wrong, stop plugin
			throw new FceSystemException(e);
		}
		return userConfigObject;
	}

	private String convertToString(ByteBuffer msg) {
		return new String(msg.array(), msg.position(), msg.limit());
	}
}
