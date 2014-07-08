package org.cloudbus.cloudsim.requestCatergory;

public class Request {
	private int requestId;
	private long initialTime;
	private long endTime;
	private double price;
	private int numberInstance;
	private RequestType type;
	private int userId;
	private long duration;
	public Request(int requestId, long initialTime, long endTime, double price,RequestType type)
	{
		this.requestId=requestId;
		this.setInitialTime(initialTime);
		this.setEndTime(endTime);
		this.setPrice(price);
		this.setType(type);
	}
	public Request(int requestId)
	{
		setRequestId(requestId);
		setEndTime(-1);
		setInitialTime(-1);
		setPrice(-1);
		setType(null);
	}
	
	public long getInitialTime() {
		return initialTime;
	}
	public void setInitialTime(long initialTime) {
		this.initialTime = initialTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public RequestType getType() {
		return type;
	}
	public void setType(RequestType type) {
		this.type = type;
	}
	public int getRequestId() {
		return requestId;
	}
	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}
	public int getNumberInstance() {
		return numberInstance;
	}
	public void setNumberInstance(int numberInstance) {
		this.numberInstance = numberInstance;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public long getDuration()
	{
		return (this.getEndTime()-this.getInitialTime());
	}
	public void setDuration(long duration){
		this.duration=duration;
	}
	

}
