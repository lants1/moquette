package org.eclipse.moquette.fce.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Contains UserConfiguration information (Restrictions, Permissions) for an user. 
 * 
 * @author lants1
 *
 */
public class UserConfiguration {

	private static final String EVERYONE = "";
	
	private String identifier;
	private ManagedPermission managePermissionType;
	private List<Restriction> publishRestrictions;
	private List<Restriction> subscribeRestrictions;
	
	public UserConfiguration(){
		this.identifier = EVERYONE;
		this.managePermissionType = ManagedPermission.EVERYONE;
		this.publishRestrictions = new ArrayList<Restriction>();
		this.subscribeRestrictions = new ArrayList<Restriction>();
	}
	
	public UserConfiguration(String identifier, ManagedPermission managePermissionType, List<Restriction> publishRestrictions, List<Restriction> subscribeRestrictions){
		this.managePermissionType = managePermissionType;
		this.publishRestrictions = publishRestrictions;
		this.subscribeRestrictions = subscribeRestrictions;
	}
	
	public ManagedPermission getManagePermissionType() {
		return managePermissionType;
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
	
	public void setManagePermissionType(ManagedPermission hasManagePermission) {
		this.managePermissionType = hasManagePermission;
	}
	
	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
}
