package org.cloudbus.cloudsim.requestCatergory;

public class ReservationWorkload {
	
	@Override
	public String toString() {
		return "ReservationWorkload [userId=" + userId + ", vms=" + vms
				+ ", startTime=" + startTime + ", endTime=" + endTime + "]";
	}

	int userId;
	int vms;
	long startTime;
	long endTime;
	
	public ReservationWorkload(int userId, int vms, long startTime, long endTime) {
		this.userId = userId;
		this.vms = vms;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public void setVms(int vms){
		this.vms = vms;
	}
	
	public int getUserId() {
		return userId;
	}

	public int getVms() {
		return vms;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}
	
	
}
