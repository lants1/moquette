package org.eclipse.moquette.fce.service;

import org.eclipse.moquette.fce.model.Restriction;
import org.eclipse.moquette.fce.model.UserConfiguration;
import org.eclipse.moquette.fce.service.parser.RestrictionAdapter;

import com.google.gson.GsonBuilder;

public class ManagedJsonParserService {

	public String serializeUserConfiguration(UserConfiguration userConfig) {
		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(Restriction.class, new RestrictionAdapter());
		return gson.create().toJson(userConfig);
	}

	public UserConfiguration deserializeUserConfiguration(String userConfigString) {
		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeAdapter(Restriction.class, new RestrictionAdapter());
		UserConfiguration userConfigObject = null;
		try {
			userConfigObject = gson.create().fromJson(userConfigString, UserConfiguration.class);
		} catch (Exception e) {
			// could happen
		}
		return userConfigObject;
	}
}
