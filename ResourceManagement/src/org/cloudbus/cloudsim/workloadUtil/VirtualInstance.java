package org.cloudbus.cloudsim.workloadUtil;
/**
 * represent a virtual instance (VM). Information needed to initiate an vm is userid, startedtime and endedtime
 * @author lnguyen2
 *
 */
public class VirtualInstance {
	private int userId;
	private long startedTime;//in microsecond by default
	private long endedTime;//in microsecond by default
	private final long PARSESECOND=1000000;
	private final long PARSEMIN=PARSESECOND*60;
	public VirtualInstance()
	{
		this.setUserId(-1);
		this.setStartedTime(-1);
		this.setEndedTime(-1);
		
	}
	/**
	 * initiating new Virtual instance.
	 * @param userId
	 * @param startedTime
	 * @param endedTime
	 */
	public VirtualInstance(int userId, long startedTime,long endedTime)
	{
		setUserId(userId);
		setStartedTime(startedTime);
		setEndedTime(endedTime);
	}
	public long getStartedTime() {
		return startedTime;
	}
	public void setStartedTime(long startedTime) {
		this.startedTime = startedTime;
	}
	public long getEndedTime() {
		return endedTime;
	}
	public void setEndedTime(long endedTime) {
		this.endedTime = endedTime;
	}
	public long getStartedTimeInSecond()
	{
		return Long.parseLong(this.getStartedTime()/PARSESECOND +"");
	}
	public long getEndedTimeInSecond()
	{
		return this.getEndedTime()/PARSESECOND;
	}
	public long getStartedTimeInMinute()
	{
		return this.getStartedTime()/PARSEMIN;
	}
	public long getEndedTimeInMinute()
	{
		return this.getEndedTime()/PARSEMIN;
	}
	public long getUsageTimeInSecond()
	{
		return(this.getEndedTimeInSecond()-this.getStartedTimeInSecond());
	}
	public long getUsageTimeInMinute()
	{
		return(this.getEndedTimeInMinute()-this.getStartedTimeInMinute());
	}
	public long getUsageTimeInMicroSecond()
	{
		return(this.getEndedTime()-this.getStartedTime());
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}

}
