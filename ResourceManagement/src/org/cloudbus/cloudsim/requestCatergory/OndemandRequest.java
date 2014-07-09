package org.cloudbus.cloudsim.requestCatergory;
/**
 * This class represents workload/instance belong to Ondemand pricing model
 * @author lnguyen2
 *
 */
public class OndemandRequest extends Request  {

	public OndemandRequest(int workloadId, long initialTime, long endTime,
			double price) {
		super(workloadId, initialTime, endTime, price, RequestType.ONDEMAND);
		// TODO Auto-generated constructor stub
	}

	public OndemandRequest(int workloadId)
	{
		super(workloadId);
		setType(RequestType.ONDEMAND);
	}

	
}
