package org.eclipse.moquette.fce.model.configuration;

import java.util.List;

import org.eclipse.moquette.fce.model.ManagedInformation;
import org.eclipse.moquette.plugin.MqttOperation;

/**
 * 
 * Contains UserConfiguration information (Restrictions, Permissions) for an user. 
 * 
 * @author lants1
 *
 */
public class UserConfiguration extends ManagedInformation {

	private ManagedState managedState;
	private ManagedPermission managePermission;
	private List<Restriction> publishRestrictions;
	private List<Restriction> subscribeRestrictions;
	
	public UserConfiguration(String userName, String userIdentifier, ManagedPermission managePermission, ManagedState managedState, List<Restriction> publishRestrictions, List<Restriction> subscribeRestrictions){
		super(userName, userIdentifier);
		this.managePermission = managePermission;
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
	
	public List<Restriction> getRestrictions(MqttOperation operation) {
		if(MqttOperation.PUBLISH == operation){
			return publishRestrictions;
		}
		if(MqttOperation.SUBSCRIBE == operation){
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

	public ManagedPermission getManagePermission() {
		return managePermission;
	}

	public void setManagePermission(ManagedPermission managePermission) {
		this.managePermission = managePermission;
	}
}
