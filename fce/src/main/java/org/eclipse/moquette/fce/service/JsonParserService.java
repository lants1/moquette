package org.eclipse.moquette.fce.service;

import java.nio.ByteBuffer;

import org.eclipse.moquette.fce.exception.FceSystemException;
import org.eclipse.moquette.fce.model.configuration.Restriction;
import org.eclipse.moquette.fce.model.configuration.UserConfiguration;
import org.eclipse.moquette.fce.model.info.InfoMessage;
import org.eclipse.moquette.fce.model.quota.UserQuota;
import org.eclipse.moquette.fce.model.quota.Quota;
import org.eclipse.moquette.fce.model.quota.IQuotaState;
import org.eclipse.moquette.fce.service.parser.QuotaAdapter;
import org.eclipse.moquette.fce.service.parser.QuotaStateAdapter;
import org.eclipse.moquette.fce.service.parser.RestrictionAdapter;

import com.google.gson.GsonBuilder;

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
