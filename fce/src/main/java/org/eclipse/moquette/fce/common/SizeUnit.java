package org.eclipse.moquette.fce.common;

import com.google.gson.annotations.SerializedName;

public enum SizeUnit {
	@SerializedName("B")
	B("B"), 
	@SerializedName("kB")
	kB("kB"),
	@SerializedName("MB")
	MB("MB"),
	@SerializedName("GB")
	GB("GB"),
	@SerializedName("TB")
	TB("TB");
	
	private String value;

    private SizeUnit(String value)
    {
        this.value=value;
    }

    public String getValue()
    {
        return(value);
    }
}
