package org.eclipse.moquette.fce.common;

import com.google.gson.annotations.SerializedName;

public enum DataUnit {
	@SerializedName("B")
	B("B", 1), 
	@SerializedName("kB")
	kB("kB", 1024),
	@SerializedName("MB")
	MB("MB", (1024*1024)),
	@SerializedName("GB")
	GB("GB", (1024*1024*1024));
	
	private String value;
	private int multiplikator;

    private DataUnit(String value, int multiplikator)
    {
        this.value=value;
        this.multiplikator=multiplikator;
    }

    public String getValue()
    {
        return(value);
    }
    
    public int getMultiplier(){
    	return multiplikator;
    }
}
