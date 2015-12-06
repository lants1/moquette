package io.moquette.fce.model.configuration;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import io.moquette.fce.model.ManagedInformation;
import io.moquette.fce.model.common.IValid;
import io.moquette.fce.model.common.ManagedScope;
import io.moquette.fce.service.FceServiceFactory;
import io.moquette.plugin.AuthorizationProperties;
import io.moquette.plugin.MqttAction;

/**
 * 
 * Contains UserConfiguration information (Restrictions, Permissions) for an
 * user.
 * 
 * @author lants1
 *
 */
public class UserConfiguration extends ManagedInformation implements IValid {

	private final ManagedState managedState;
	private final ManagedScope managedScope;
	private final AdminPermission adminPermission;
	private final FceAction actionPermission;
	private final List<Restriction> restrictions;

	public UserConfiguration(String alias, String usernameHash, FceAction actionPermission,
			AdminPermission adminPermission, ManagedState managedState, ManagedScope scope,
			List<Restriction> restrictions) {
		super(alias, usernameHash);
		this.actionPermission = actionPermission;
		this.restrictions = restrictions;
		this.managedState = managedState;
		this.managedScope = scope;
		this.adminPermission = adminPermission;
	}

	public ManagedState getManagedState() {
		return managedState;
	}

	public List<Restriction> getRestrictions() {
		if(restrictions == null || restrictions.isEmpty()){
			return new ArrayList<>();
		}
		return restrictions;
	}

	public void addRestriction(Restriction restriction) {
		restrictions.add(restriction);
	}

	public List<Restriction> getRestrictions(MqttAction operation) {
		List<Restriction> results = new ArrayList<>();
		for (Restriction restriction : getRestrictions()) {
			if (restriction.getMqttAction().canDoOperation(operation)) {
				results.add(restriction);
			}
		}
		return results;
	}

	public FceAction getActionPermission() {
		return actionPermission;
	}

	public ManagedScope getManagedScope() {
		return managedScope;
	}

	public AdminPermission getAdminPermission() {
		return adminPermission;
	}

	@Override
	public boolean isValid(FceServiceFactory services, AuthorizationProperties props, MqttAction operation) {
		List<Restriction> restrictions = getRestrictions(operation);

		if (!getActionPermission().canDoOperation(operation)) {
			return false;
		}

		if (restrictions.isEmpty()) {
			return true;
		}

		for (Restriction restriction : restrictions) {
			if (!restriction.isValid(services, props, operation)) {
				return false;
			}
		}

		return true;
	}

	public List<String> getSchemaTopics() {
		List<Restriction> restrictions = getRestrictions();
		List<String> resultSchemaTopics = new ArrayList<>();
		
		for (Restriction restriction : restrictions) {
			if (restriction.getDataSchema() != null && StringUtils.isNotEmpty(restriction.getDataSchema().getSchemaTopic())) {
				resultSchemaTopics.add(restriction.getDataSchema().getSchemaTopic());
			}
		}

		return resultSchemaTopics;
	}
	
	public boolean isValidForEveryone() {
		return StringUtils.isEmpty(getUserHash());
	}
	
	public boolean hasQuota(){
		if(this.getRestrictions().isEmpty()){
			return false;
		}
		return true;
	}
}
