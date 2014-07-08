package org.cloudbus.cloudsim.requestCatergory;

import javax.swing.JOptionPane;

/**
 * This class represents workload/instance belong to Reservation pricing model
 * @author lnguyen2
 *
 */
public class ReservedRequest extends Request {

	public ReservedRequest(int workloadId, long initialTime, long endTime,
			double price,RequestType type) {
		super(workloadId, initialTime, endTime, price,type);
		// TODO Auto-generated constructor stub
	}
	public ReservedRequest(int workloadId){
		super(workloadId);
	}

	
	


}
