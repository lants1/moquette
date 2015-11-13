package org.eclipse.moquette.fce.service;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.moquette.fce.common.SizeUnit;
import org.eclipse.moquette.fce.model.ManagedCycle;
import org.eclipse.moquette.fce.model.configuration.ManagedPermission;
import org.eclipse.moquette.fce.model.configuration.PeriodicRestriction;
import org.eclipse.moquette.fce.model.configuration.Restriction;
import org.eclipse.moquette.fce.model.configuration.SpecificRestriction;
import org.eclipse.moquette.fce.model.configuration.UserConfiguration;
import org.eclipse.moquette.fce.model.quota.PeriodicQuotaState;
import org.eclipse.moquette.fce.model.quota.Quota;
import org.eclipse.moquette.fce.model.quota.QuotaState;
import org.eclipse.moquette.fce.model.quota.SpecificQuotaState;
import org.junit.Before;
import org.junit.Test;

public class TestManagedJsonParser {

	Date sampleDate = null;
	
	@Before
	public void init(){
		String date_s = " 2015-01-18 00:00:00.0";
		SimpleDateFormat dt = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		try {
			sampleDate = dt.parse(date_s);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testSerializationAndDeserializationRestriction() throws IOException, URISyntaxException {
		SpecificRestriction specificRestriction = new SpecificRestriction(sampleDate, sampleDate, 11, 1024, 2048, SizeUnit.kB);
		PeriodicRestriction periodicRestriction = new PeriodicRestriction(ManagedCycle.DAILY, 11, 1024, 2048, SizeUnit.kB);

		List<Restriction> publishRestriction = new ArrayList<Restriction>();
		List<Restriction> subscribeRestriction = new ArrayList<Restriction>();

		publishRestriction.add(specificRestriction);
		publishRestriction.add(periodicRestriction);

		subscribeRestriction.add(specificRestriction);
		subscribeRestriction.add(periodicRestriction);

		UserConfiguration sampleUserConfig = new UserConfiguration("swen", ManagedPermission.NONE, publishRestriction,
				subscribeRestriction);

		FceJsonParserService mJsonParser = new FceJsonParserService();
		String json = mJsonParser.serialize(sampleUserConfig);
		
		UserConfiguration sampleUserConfigDeserialized = mJsonParser.deserializeUserConfiguration(json);
		assertTrue(sampleUserConfigDeserialized.getIdentifier().equalsIgnoreCase("swen"));
		assertTrue(sampleUserConfigDeserialized.getManagedPermissionType() == ManagedPermission.NONE);
		assertTrue(sampleUserConfigDeserialized.getPublishRestrictions().size() == 2);
		assertTrue(sampleUserConfigDeserialized.getSubscribeRestrictions().size() == 2);
		
		assertTrue(((SpecificRestriction) sampleUserConfigDeserialized.getPublishRestrictions().get(0)).getFrom().equals(sampleDate));
		assertTrue(((PeriodicRestriction) sampleUserConfigDeserialized.getSubscribeRestrictions().get(1)).getCyle() == ManagedCycle.DAILY);
	}

	@Test
	public void testSerializationAndDeserializationQuota() throws IOException, URISyntaxException {
		QuotaState specificQuotaState = new SpecificQuotaState(sampleDate, sampleDate, 11, 1024, 12, 200, SizeUnit.kB);
		QuotaState periodicQuotaState = new PeriodicQuotaState(ManagedCycle.DAILY, 11, 1024, 12, 200, SizeUnit.kB);

		List<QuotaState> quotas = new ArrayList<>();
		quotas.add(specificQuotaState);
		quotas.add(periodicQuotaState);
		
		Quota quota = new Quota("bla", quotas);

		FceJsonParserService mJsonParser = new FceJsonParserService();
		String serializedQuota = mJsonParser.serialize(quota);
		
		Quota deserializedQuota = mJsonParser.deserializeQuotaState(serializedQuota);
		
		deserializedQuota.getUsergroup().equalsIgnoreCase("bla");
		
		assertTrue(((SpecificQuotaState) deserializedQuota.getQuotaState().get(0)).getTo().equals(sampleDate));
		assertTrue(((PeriodicQuotaState) deserializedQuota.getQuotaState().get(1)).getCycle() == ManagedCycle.DAILY);
	}
	
	
	@Test
	public void testDeserializationFromSample() throws IOException, URISyntaxException {
		FceJsonParserService mJsonParser = new FceJsonParserService();
		String inputJson = readFile("/sample_manage.json");
		UserConfiguration sampleUserConfig = mJsonParser.deserializeUserConfiguration(inputJson);
		assertTrue(sampleUserConfig.getIdentifier().equalsIgnoreCase("swen"));
		assertTrue(sampleUserConfig.getManagedPermissionType() == ManagedPermission.NONE);
		assertTrue(sampleUserConfig.getPublishRestrictions().size() == 2);
		assertTrue(sampleUserConfig.getSubscribeRestrictions().size() == 2);
		
		assertTrue(((SpecificRestriction) sampleUserConfig.getPublishRestrictions().get(0)).getFrom().equals(sampleDate));
		assertTrue(((PeriodicRestriction) sampleUserConfig.getSubscribeRestrictions().get(1)).getCyle() == ManagedCycle.DAILY);
	}
	
	@Test
	public void testDeserializationFailure() throws IOException, URISyntaxException {
		FceJsonParserService mJsonParser = new FceJsonParserService();
		UserConfiguration sampleUserConfig = mJsonParser.deserializeUserConfiguration("asfdasdfsa");
		assertNull(sampleUserConfig);
	}

	static String readFile(String path) throws IOException, URISyntaxException {
		byte[] encoded = Files.readAllBytes(Paths.get(TestManagedJsonParser.class.getResource(path).toURI()));
		return new String(encoded, StandardCharsets.UTF_8);
	}
}
