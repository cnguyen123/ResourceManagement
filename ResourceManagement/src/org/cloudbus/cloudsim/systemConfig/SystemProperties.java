package org.cloudbus.cloudsim.systemConfig;



/**
 * This enumeration lists all the system properties
 * @author lnguyen2
 *
 */
public enum SystemProperties {
	//for simulation setting
	SIMULATION_HOUR("simulation.hours",SystemPropertyType.INTEGER),
	GLOBAL_SEED("global.seed",SystemPropertyType.INTEGER),
	
	//for resource management task setting
	RESOURCE_MANAGEMENT_RESERVED_LENGTH("revenueManagement.reservation.length",SystemPropertyType.INTEGER),
	RESOURCE_MANAGEAMENT_WARMUP("revenueManagement.warmup.time",SystemPropertyType.INTEGER),
	RESOURCE_MANAGEMENT_RESERVED_PREMIUM("revenueManagement.reserved.premium",SystemPropertyType.INTEGER),
	RESOURCE_MANAGEMENT_TIME_SLOT("revenueManagement.timeslot.size",SystemPropertyType.INTEGER),
	RESOURCE_MANAGEMENT_ONDEMAND_PRICE("revenueManagement.ondemand.price",SystemPropertyType.DOUBLE),
	RESOURCE_MANAGEMENT_RESERVED_DISCOUNT_FACTOR("revenueManagement.reserved.discount.factor",SystemPropertyType.DOUBLE),
	RESOURCE_MANAGEMENT_SPOT_DISCOUNT_FACTOR("revenueManagement.spot.discount.factor",SystemPropertyType.DOUBLE),
	RESOURCE_MANAGEMENT_DATACENTER_CAPACITY("revenueManagement.capacity",SystemPropertyType.LONG),
	RESOURCE_MANAGEMENT_SLAVIOLATION_COMPENSATION("revenueManagement.SLAviolation.compensation",SystemPropertyType.DOUBLE),
	RESOURCE_MANAGEMENT_OVERBOOKING_THRESHOLD("revenueManagement.overbook.threshold",SystemPropertyType.DOUBLE)
	;
	
	//TODO more attributes will be added later 
	;
	private String key;
	private SystemPropertyType type;
	private SystemProperties(String k, SystemPropertyType t) {
		this.key=k;
		this.type=t;
		
	}
	public String getValue()
	{
		return SystemConfiguration.getInstance().getProperty(this.key);
	}
	
	public int getValueAsInt()
	{
		if(this.type!=SystemPropertyType.INTEGER)
		{
			System.out.println("Error happen, "+this.key+" is not an integer value.");
			System.exit(2);
		}
		return Integer.parseInt(this.getValue());
	}
	public boolean getValueAsBool()
	{
		if(this.type!=SystemPropertyType.BOOLEAN)
		{
			System.out.println("Error happen, "+this.key+" is not a boolean value.");
			System.exit(2);
		}
		return Boolean.parseBoolean(this.getValue());
	}
	

	public long getValueAsLong()
	{
	
		if(this.type!=SystemPropertyType.LONG)
	
		{
		
			System.out.println("Error happen, "+this.key+" is not a long value.");
		
			System.exit(2);
	
		}
	
		return Long.parseLong(this.getValue());

	}
	public double getValueAsDouble()
	{
		if(this.type!=SystemPropertyType.DOUBLE)
		{
			System.out.println("Error happen, "+this.key+" is not a double value.");
			System.exit(2);
		}
		return Double.parseDouble(this.getValue());
	}
	
	public void setValue(String value)
	{
		SystemConfiguration.getInstance().setProperties(this.key, value);
	}
	public void setValue(boolean value)
	{
		SystemConfiguration.getInstance().setProperties(this.key, Boolean.toString(value));
	}
	
	public void setValue(int value)
	{
		SystemConfiguration.getInstance().setProperties(this.key, Integer.toString(value));
	}
	public void setValue(double value)
	{
		SystemConfiguration.getInstance().setProperties(this.key, Double.toString(value));
	}
	public void setValue(long value)
	{
		SystemConfiguration.getInstance().setProperties(this.key, Long.toString(value));
	}
	@Override
	public String toString() {
		return this.key + "=" + this.getValue();
	}
	public String getKey()
	{
		return this.key;
	}
	public SystemPropertyType getType()
	{
		return this.type;
	}
	public boolean alreadySet()
	{
		return (SystemConfiguration.getInstance().getProperty(key)!=null);
	}
	
	

}
