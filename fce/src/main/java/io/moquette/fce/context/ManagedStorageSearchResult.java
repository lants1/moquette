package io.moquette.fce.context;

import io.moquette.fce.model.ManagedInformation;

/**
 * ResultTO for a manage storage search.
 * 
 * @author lants1
 *
 */
public class ManagedStorageSearchResult {

	private String storageLocation;
	private ManagedInformation data;

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