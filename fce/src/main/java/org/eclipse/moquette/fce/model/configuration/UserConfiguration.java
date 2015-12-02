package org.eclipse.moquette.fce.model.configuration;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.moquette.fce.model.ManagedInformation;
import org.eclipse.moquette.fce.model.common.IValid;
import org.eclipse.moquette.fce.model.common.ManagedScope;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.MqttAction;

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
	public boolean isValid(AuthorizationProperties props, MqttAction operation) {
		List<Restriction> restrictions = getRestrictions(operation);

		if (!getActionPermission().canDoOperation(operation)) {
			return false;
		}

		if (restrictions.isEmpty()) {
			return true;
		}

		for (Restriction restriction : restrictions) {
			if (!restriction.isValid(props, operation)) {
				return false;
			}
		}

		return true;
	}

	public boolean isValidForEveryone() {
		return StringUtils.isEmpty(getUserHash());
	}
}
