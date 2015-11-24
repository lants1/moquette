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

import org.eclipse.moquette.fce.common.DataUnit;
import org.eclipse.moquette.fce.model.ManagedCycle;
import org.eclipse.moquette.fce.model.ManagedScope;
import org.eclipse.moquette.fce.model.configuration.AdminPermission;
import org.eclipse.moquette.fce.model.configuration.FceAction;
import org.eclipse.moquette.fce.model.configuration.ManagedState;
import org.eclipse.moquette.fce.model.configuration.PeriodicRestriction;
import org.eclipse.moquette.fce.model.configuration.Restriction;
import org.eclipse.moquette.fce.model.configuration.TimeframeRestriction;
import org.eclipse.moquette.fce.model.configuration.UserConfiguration;
import org.eclipse.moquette.fce.model.quota.PeriodicQuota;
import org.eclipse.moquette.fce.model.quota.UserQuota;
import org.eclipse.moquette.plugin.MqttAction;
import org.eclipse.moquette.fce.model.quota.Quota;
import org.eclipse.moquette.fce.model.quota.TimeframeQuota;
import org.eclipse.moquette.fce.model.quota.TransmittedDataState;
import org.junit.Before;
import org.junit.Test;

public class JsonParserServiceTest {

	private static final String TESTIDENTIFIER = "testidentifier";
	private static final String TESTUSER = "testuser";
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
		TimeframeRestriction specificRestriction = new TimeframeRestriction(FceAction.ALL, sampleDate, sampleDate, 11, 1024, 2048, DataUnit.kB, "");
		PeriodicRestriction periodicRestriction = new PeriodicRestriction(FceAction.ALL, ManagedCycle.DAILY, 11, 1024, 2048, DataUnit.kB, "");

		List<Restriction> restrictions = new ArrayList<Restriction>();

		restrictions.add(specificRestriction);
		restrictions.add(periodicRestriction);

		UserConfiguration sampleUserConfig = new UserConfiguration(TESTUSER, TESTIDENTIFIER, FceAction.ALL, AdminPermission.ALL, ManagedState.ACTIVE, ManagedScope.GLOBAL, restrictions);

		JsonParserService mJsonParser = new JsonParserService();
		String json = mJsonParser.serialize(sampleUserConfig);
		
		System.out.println(json);
		
		UserConfiguration sampleUserConfigDeserialized = mJsonParser.deserializeUserConfiguration(json);
		assertTrue(sampleUserConfigDeserialized.getUserIdentifier().equalsIgnoreCase(TESTIDENTIFIER));
		assertTrue(sampleUserConfigDeserialized.getActionPermission() == FceAction.ALL);
		assertTrue(sampleUserConfigDeserialized.getRestrictions().size() == 2);
		
		assertTrue(((TimeframeRestriction) sampleUserConfigDeserialized.getRestrictions().get(0)).getFrom().equals(sampleDate));
		assertTrue(((PeriodicRestriction) sampleUserConfigDeserialized.getRestrictions().get(1)).getCyle() == ManagedCycle.DAILY);
	}

	@Test
	public void testSerializationAndDeserializationQuota() throws IOException, URISyntaxException {
		Quota specificQuotaState = new TimeframeQuota(sampleDate, sampleDate, new TransmittedDataState(2, 2));
		Quota periodicQuotaState = new PeriodicQuota(ManagedCycle.DAILY, new TransmittedDataState(2, 2));

		List<Quota> quotas = new ArrayList<>();
		quotas.add(specificQuotaState);
		quotas.add(periodicQuotaState);
		
		UserQuota quota = new UserQuota(TESTUSER, TESTIDENTIFIER, MqttAction.PUBLISH, quotas);

		JsonParserService mJsonParser = new JsonParserService();
		String serializedQuota = mJsonParser.serialize(quota);
		
		UserQuota deserializedQuota = mJsonParser.deserializeQuota(serializedQuota);
		
		deserializedQuota.getUserIdentifier().equalsIgnoreCase(TESTIDENTIFIER);
		
		assertTrue(((TimeframeQuota) deserializedQuota.getQuotas().get(0)).getTo().equals(sampleDate));
		assertTrue(((PeriodicQuota) deserializedQuota.getQuotas().get(1)).getCycle() == ManagedCycle.DAILY);
	}
	
	
	@Test
	public void testDeserializationFromSample() throws IOException, URISyntaxException {
		JsonParserService mJsonParser = new JsonParserService();
		String inputJson = readFile("/sample_manage.json");
		UserConfiguration sampleUserConfig = mJsonParser.deserializeUserConfiguration(inputJson);
		assertTrue(sampleUserConfig.getUserIdentifier().equalsIgnoreCase(TESTIDENTIFIER));
		assertTrue(sampleUserConfig.getActionPermission() == FceAction.ALL);
		assertTrue(sampleUserConfig.getRestrictions().size() == 2);
		
		assertTrue(((TimeframeRestriction) sampleUserConfig.getRestrictions().get(0)).getFrom().equals(sampleDate));
		assertTrue(((PeriodicRestriction) sampleUserConfig.getRestrictions().get(1)).getCyle() == ManagedCycle.DAILY);
	}
	
	@Test
	public void testDeserializationFailure() throws IOException, URISyntaxException {
		JsonParserService mJsonParser = new JsonParserService();
		UserConfiguration sampleUserConfig = mJsonParser.deserializeUserConfiguration("asfdasdfsa");
		assertNull(sampleUserConfig);
	}

	static String readFile(String path) throws IOException, URISyntaxException {
		byte[] encoded = Files.readAllBytes(Paths.get(JsonParserServiceTest.class.getResource(path).toURI()));
		return new String(encoded, StandardCharsets.UTF_8);
	}
}
