package org.cloudbus.cloudsim.requestCatergory;
/**
 * This class represents workload/instance belong to Spot pricing model
 * @author lnguyen2
 *
 */
public class SpotRequest extends Request{

	public SpotRequest(int workloadId, long initialTime, long endTime,
			double price, RequestType type) {
		super(workloadId, initialTime, endTime, price,type);
		// TODO Auto-generated constructor stub
	}

	public SpotRequest(int workloadId)
	{
		super(workloadId);
	}

	

}
