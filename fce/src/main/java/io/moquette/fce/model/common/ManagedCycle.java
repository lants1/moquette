package io.moquette.fce.model.common;

import java.util.Calendar;

import com.google.gson.annotations.SerializedName;

/**
 * Define all type of ManagedCycles.
 * 
 * @author lants1
 *
 */
public enum ManagedCycle {
	@SerializedName("HOURLY")
	HOURLY("HOURLY", Calendar.HOUR_OF_DAY),
	@SerializedName("DAILY")
	DAILY("DAILY", Calendar.DAY_OF_YEAR),
	@SerializedName("WEEKLY")
	WEEKLY("WEEKLY", Calendar.WEEK_OF_YEAR),
	@SerializedName("MONTHLY")
	MONTHLY("MONTHLY", Calendar.MONTH), 
	@SerializedName("YEARLY")
	YEARLY("YEARLY", Calendar.YEAR);
	
	private String value;
	private int calendarReference;

    private ManagedCycle(String value, int calendarReference)
    {
        this.value=value;
        this.calendarReference=calendarReference;
    }

    /**
     * Gets the value of a ManagedCycle enum.
     * 
     * @return String value of an enum
     */
    public String getValue()
    {
        return value;
    }
    
    /**
     * Gets the calender reference of an enum.
     * 
     * @return int Calendar.Reference
     */
    public int getCalendarReference(){
    	return calendarReference;
    }
}
