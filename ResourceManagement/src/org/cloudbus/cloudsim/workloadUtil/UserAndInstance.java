package org.cloudbus.cloudsim.workloadUtil;

public class UserAndInstance {
	private int userId;
	private long startedTime;
	private long endedTime;
	public UserAndInstance(int userId,long startedTime,long endedTime)
	{
		setEndedTime(endedTime);
		setStartedTime(startedTime);
		setUserId(userId);
	}
	public long getEndedTime() {
		return endedTime;
	}
	private void setEndedTime(long endedTime) {
		this.endedTime = endedTime;
	}
	public int getUserId() {
		return userId;
	}
	private void setUserId(int userId) {
		this.userId = userId;
	}
	public long getStartedTime() {
		return startedTime;
	}
	private void setStartedTime(long startedTime) {
		this.startedTime = startedTime;
	}
	@Override
	public boolean equals( Object u2)
	{
		UserAndInstance user2=(UserAndInstance)u2;
		if(this.getUserId()==user2.getUserId()&& this.getEndedTime()==user2.getEndedTime()&& this.getStartedTime()==user2.getStartedTime())
		return true;
			return false;
	}
	@Override
	public int hashCode()
	{
		int hash=this.getUserId()*30+1;
		return hash;
	}
	
	

}
