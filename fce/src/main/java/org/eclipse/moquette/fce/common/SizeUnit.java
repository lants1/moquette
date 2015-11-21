package org.eclipse.moquette.fce.common;

import com.google.gson.annotations.SerializedName;

public enum SizeUnit {
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

    private SizeUnit(String value, int multiplikator)
    {
        this.value=value;
        this.multiplikator=multiplikator;
    }

    public String getValue()
    {
        return(value);
    }
    
    public int getMultiplikator(){
    	return multiplikator;
    }
}
