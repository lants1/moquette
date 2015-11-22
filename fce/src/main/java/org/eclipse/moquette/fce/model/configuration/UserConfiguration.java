package org.eclipse.moquette.fce.model.configuration;

import java.util.List;

import org.eclipse.moquette.fce.model.IValid;
import org.eclipse.moquette.fce.model.ManagedInformation;
import org.eclipse.moquette.plugin.AuthorizationProperties;
import org.eclipse.moquette.plugin.MqttAction;

/**
 * 
 * Contains UserConfiguration information (Restrictions, Permissions) for an user. 
 * 
 * @author lants1
 *
 */
public class UserConfiguration extends ManagedInformation implements IValid{

	private ManagedState managedState;
	private AdminGroupPermission adminGroupPermission;
	private AdminActionPermission adminActionPermission;
	private ActionPermission actionPermission;
	private List<Restriction> publishRestrictions;
	private List<Restriction> subscribeRestrictions;
	
	public UserConfiguration(String userName, String userIdentifier, ActionPermission actionPermission, ManagedState managedState, List<Restriction> publishRestrictions, List<Restriction> subscribeRestrictions){
		super(userName, userIdentifier);
		this.actionPermission = actionPermission;
		this.publishRestrictions = publishRestrictions;
		this.subscribeRestrictions = subscribeRestrictions;
		this.managedState = managedState;
	}
	
	public ManagedState getManagedState() {
		return managedState;
	}

	public void setManagedState(ManagedState managedState) {
		this.managedState = managedState;
	}

	public List<Restriction> getPublishRestrictions() {
		return publishRestrictions;
	}
	
	public void setPublishRestrictions(List<Restriction> publishRestrictions) {
		this.publishRestrictions = publishRestrictions;
	}
	
	public void addPublishRestriction(Restriction publishRestriction) {
		this.subscribeRestrictions.add(publishRestriction);
	}
	
	public List<Restriction> getSubscribeRestrictions() {
		return subscribeRestrictions;
	}
	
	public List<Restriction> getRestrictions(MqttAction operation) {
		if(MqttAction.PUBLISH == operation){
			return publishRestrictions;
		}
		if(MqttAction.SUBSCRIBE == operation){
			return subscribeRestrictions;
		}
		return null;
	}
	
	public void setSubscribeRestrictions(List<Restriction> subscribeRestrictions) {
		this.subscribeRestrictions = subscribeRestrictions;
	}
	
	public void addSubscribeRestriction(Restriction subscribeRestriction) {
		this.subscribeRestrictions.add(subscribeRestriction);
	}


	public AdminGroupPermission getAdminGroupPermission() {
		return adminGroupPermission;
	}

	public void setAdminGroupPermission(AdminGroupPermission adminGroupPermission) {
		this.adminGroupPermission = adminGroupPermission;
	}

	public AdminActionPermission getAdminActionPermission() {
		return adminActionPermission;
	}

	public void setAdminActionPermission(AdminActionPermission adminActionPermission) {
		this.adminActionPermission = adminActionPermission;
	}

	public ActionPermission getActionPermission() {
		return actionPermission;
	}

	public void setActionPermission(ActionPermission actionPermission) {
		this.actionPermission = actionPermission;
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
		
		return false;
	}
}
