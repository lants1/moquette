package io.moquette.fce.context;

import io.moquette.fce.model.ManagedInformation;

public class ManagedStorageSearchResult {

	public String storageLocation;
	public ManagedInformation data;
	
	public ManagedStorageSearchResult(String storageLocation, ManagedInformation data) {
		super();
		this.storageLocation = storageLocation;
		this.data = data;
	}
	public String getStorageLocation() {
		return storageLocation;
	}
	public void setStorageLocation(String storageLocation) {
		this.storageLocation = storageLocation;
	}
	public ManagedInformation getData() {
		return data;
	}
	public void setData(ManagedInformation data) {
		this.data = data;
	}
	
	
}
