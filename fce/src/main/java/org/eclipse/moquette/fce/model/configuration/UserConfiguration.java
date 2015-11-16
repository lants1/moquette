package org.eclipse.moquette.fce.model.configuration;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.moquette.fce.model.ManagedInformation;

/**
 * 
 * Contains UserConfiguration information (Restrictions, Permissions) for an user. 
 * 
 * @author lants1
 *
 */
public class UserConfiguration extends ManagedInformation {

	private ManagedPermission managedPermissionType;
	private ManagedState managedState;
	private List<Restriction> publishRestrictions;
	private List<Restriction> subscribeRestrictions;
	
	public UserConfiguration(String userName, String userIdentifier, ManagedPermission managedPermissionType, ManagedState managedState, List<Restriction> publishRestrictions, List<Restriction> subscribeRestrictions){
		super(userName, userIdentifier);
		this.managedPermissionType = managedPermissionType;
		this.publishRestrictions = publishRestrictions;
		this.subscribeRestrictions = subscribeRestrictions;
		this.managedState = managedState;
	}
	
	public ManagedPermission getManagedPermissionType() {
		return managedPermissionType;
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
	
	public void setSubscribeRestrictions(List<Restriction> subscribeRestrictions) {
		this.subscribeRestrictions = subscribeRestrictions;
	}
	
	public void addSubscribeRestriction(Restriction subscribeRestriction) {
		this.subscribeRestrictions.add(subscribeRestriction);
	}
	
	public void setManagedPermissionType(ManagedPermission hasManagedPermission) {
		this.managedPermissionType = hasManagedPermission;
	}
	
}
