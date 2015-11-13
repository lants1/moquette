package org.eclipse.moquette.fce.service;

import org.eclipse.moquette.fce.model.configuration.Restriction;
import org.eclipse.moquette.fce.model.configuration.UserConfiguration;
import org.eclipse.moquette.fce.model.info.InfoMessage;
import org.eclipse.moquette.fce.model.quota.Quota;
import org.eclipse.moquette.fce.model.quota.QuotaState;
import org.eclipse.moquette.fce.service.parser.QuotaStateAdapter;
import org.eclipse.moquette.fce.service.parser.RestrictionAdapter;

import com.google.gson.GsonBuilder;

public class FceJsonParserService {

	public String serialize(UserConfiguration userConfig) {
		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(Restriction.class, new RestrictionAdapter());
		return gson.create().toJson(userConfig);
	}
	
	public String serialize(Quota quota) {
		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(QuotaState.class, new QuotaStateAdapter());
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
			// could happen because the UserConfiguration comes from the user itself...
		}
		return userConfigObject;
	}
	
	public Quota deserializeQuotaState(String quotaStateString) {
		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(QuotaState.class, new QuotaStateAdapter());
		Quota userConfigObject = null;
		try {
			userConfigObject = gson.create().fromJson(quotaStateString, Quota.class);
		} catch (Exception e) {
			// something evil is wrong, stop plugin
			throw new RuntimeException(e);
		}
		return userConfigObject;
	}
	
	public InfoMessage deserializeInfoMessage(String infoMessageString) {
		GsonBuilder gson = new GsonBuilder();
		InfoMessage userConfigObject = null;
		try {
			userConfigObject = gson.create().fromJson(infoMessageString, InfoMessage.class);
		} catch (Exception e) {
			// something evil is wrong, stop plugin
			throw new RuntimeException(e);
		}
		return userConfigObject;
	}
}
