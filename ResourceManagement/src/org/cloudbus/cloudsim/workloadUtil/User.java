package org.cloudbus.cloudsim.workloadUtil;
/**
 * represent a user who wants to use resource (Vm) in cloud data center
 * @author lnguyen2
 *
 */
public class User {
	private int userId;
	private int userLevel;
	public User()
	{
		setUserId(-1);
		setUserLevel(-1);
	}
	public User(int userId,int userLevel)
	{
		setUserId(userId);
		setUserLevel(userLevel);
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getUserLevel() {
		return userLevel;
	}
	public void setUserLevel(int userLevel) {
		this.userLevel = userLevel;
	}
	@Override
	public boolean equals(Object o2)
	{
		User u2=(User)o2;
		if(u2.getUserId()==this.getUserId()&& u2.getUserLevel()==this.getUserLevel())
			return true;
		return false;
	}
	@Override
	public int hashCode()
	{
		int hash=-1;
		hash=this.getUserId()*30+1;
		return hash;
	}

}
