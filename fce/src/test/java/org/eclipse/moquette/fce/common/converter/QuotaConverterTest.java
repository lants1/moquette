package org.eclipse.moquette.fce.common.converter;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.moquette.fce.model.common.DataUnit;
import org.eclipse.moquette.fce.model.common.ManagedScope;
import org.eclipse.moquette.fce.model.configuration.AdminPermission;
import org.eclipse.moquette.fce.model.configuration.FceAction;
import org.eclipse.moquette.fce.model.configuration.ManagedState;
import org.eclipse.moquette.fce.model.configuration.PeriodicRestriction;
import org.eclipse.moquette.fce.model.configuration.Restriction;
import org.eclipse.moquette.fce.model.configuration.TimeframeRestriction;
import org.eclipse.moquette.fce.model.configuration.UserConfiguration;
import org.eclipse.moquette.fce.model.quota.Quota;
import org.eclipse.moquette.fce.model.quota.UserQuota;
import org.junit.Test;

public class QuotaConverterTest {

	@Test
	public void testWithRestrictions() {
		List<Restriction> restrictions = new ArrayList<>();
		TimeframeRestriction tfRest = new TimeframeRestriction(FceAction.PUBLISH, null, null, 1, 1, 1, DataUnit.B, null);
		PeriodicRestriction perRest = new PeriodicRestriction(FceAction.SUBSCRIBE, null, 1, 1, 1, DataUnit.B, null);
		restrictions.add(tfRest);
		restrictions.add(perRest);
		UserConfiguration userConfig = new UserConfiguration("alias","userhash",FceAction.ALL, AdminPermission.ALL, ManagedState.ACTIVE, ManagedScope.GLOBAL,restrictions);
		List<Quota> quotas = QuotaConverter.convertRestrictions(restrictions);
		assertTrue(quotas.size()==4);
		UserQuota usrQuota = QuotaConverter.convertPublishConfiguration(userConfig);
		assertTrue(usrQuota.getQuotas().size() == 2);
		usrQuota = QuotaConverter.convertSubscribeConfiguration(userConfig);
		assertTrue(usrQuota.getQuotas().size() == 2);
	}
	
	@Test
	public void testWithoutRestriction() {
		UserConfiguration userConfig = new UserConfiguration("alias","userhash",FceAction.ALL, AdminPermission.ALL, ManagedState.ACTIVE, ManagedScope.GLOBAL,null);
		List<Quota> quotas = QuotaConverter.convertRestrictions(null);
		assertTrue(quotas.size()==0);
		UserQuota usrQuota = QuotaConverter.convertPublishConfiguration(userConfig);
		assertTrue(usrQuota.getQuotas().size() == 0);
		usrQuota = QuotaConverter.convertSubscribeConfiguration(userConfig);
		assertTrue(usrQuota.getQuotas().size() == 0);
	}

	@Test
	public void testWithEmptyRestriction() {
		List<Restriction> restrictions = new ArrayList<>();
		TimeframeRestriction tfRest = new TimeframeRestriction(FceAction.PUBLISH, null, null, 0, 0, 0, DataUnit.B, "asdf");
		PeriodicRestriction perRest = new PeriodicRestriction(FceAction.SUBSCRIBE, null, 0, 0, 0, DataUnit.B, "asdfasf");
		restrictions.add(tfRest);
		restrictions.add(perRest);
		UserConfiguration userConfig = new UserConfiguration("alias","userhash",FceAction.ALL, AdminPermission.ALL, ManagedState.ACTIVE, ManagedScope.GLOBAL,restrictions);
		List<Quota> quotas = QuotaConverter.convertRestrictions(restrictions);
		assertTrue(quotas.size()==0);
		UserQuota usrQuota = QuotaConverter.convertPublishConfiguration(userConfig);
		assertTrue(usrQuota.getQuotas().size() == 0);
		usrQuota = QuotaConverter.convertSubscribeConfiguration(userConfig);
		assertTrue(usrQuota.getQuotas().size() == 0);
	}
	
	@Test
	public void testWithMinusRestriction() {
		List<Restriction> restrictions = new ArrayList<>();
		TimeframeRestriction tfRest = new TimeframeRestriction(FceAction.PUBLISH, null, null, -1, -1, -1, DataUnit.B, "asdf");
		PeriodicRestriction perRest = new PeriodicRestriction(FceAction.SUBSCRIBE, null, -1, -1, -1, DataUnit.B, "asdfasf");
		restrictions.add(tfRest);
		restrictions.add(perRest);
		UserConfiguration userConfig = new UserConfiguration("alias","userhash",FceAction.ALL, AdminPermission.ALL, ManagedState.ACTIVE, ManagedScope.GLOBAL,restrictions);
		List<Quota> quotas = QuotaConverter.convertRestrictions(restrictions);
		assertTrue(quotas.size()==4);
		UserQuota usrQuota = QuotaConverter.convertPublishConfiguration(userConfig);
		assertTrue(usrQuota.getQuotas().size() == 2);
		usrQuota = QuotaConverter.convertSubscribeConfiguration(userConfig);
		assertTrue(usrQuota.getQuotas().size() == 2);
	}
}
