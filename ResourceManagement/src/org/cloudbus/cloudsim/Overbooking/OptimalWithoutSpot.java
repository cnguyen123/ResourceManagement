package org.cloudbus.cloudsim.Overbooking;

import java.util.ArrayList;
import java.util.Random;

import org.cloudbus.cloudsim.requestCatergory.ReservedRequest;
import org.cloudbus.cloudsim.systemConfig.SystemProperties;

public class OptimalWithoutSpot {
	private long[] numberOfReserved;//array keeps values of Reserved demand prediction in timewindow 
	private long[] numberOfOndemand;//array keeps values of Reserved demand prediction in timewindow 
	private final int timeWindow=SystemProperties.RESOURCE_MANAGEMENT_TIME_SLOT.getValueAsInt();
	private final long CAPACITY=SystemProperties.RESOURCE_MANAGEMENT_DATACENTER_CAPACITY.getValueAsLong();
	private final double reservedDiscount=SystemProperties.RESOURCE_MANAGEMENT_RESERVED_DISCOUNT_FACTOR.getValueAsDouble();
	private final double spotDiscount=SystemProperties.RESOURCE_MANAGEMENT_SPOT_DISCOUNT_FACTOR.getValueAsDouble();
	private final double ondemandPrice=SystemProperties.RESOURCE_MANAGEMENT_ONDEMAND_PRICE.getValueAsDouble();
	private final double overbooking_threshold=SystemProperties.RESOURCE_MANAGEMENT_OVERBOOKING_THRESHOLD.getValueAsDouble();
	/*this matrix is to memorize optimal results represents number requests accepted and the revenue at time slot t
	[numberOndemand][numberReserved][revenue]
	 * */
	int [][][]optimalMatrix = null;
	int [][][]tempMatrix;
	double maxiRevenue=0;
	private void initialOptimalMatrix()
	{
		optimalMatrix=new int[timeWindow][timeWindow][timeWindow];
		tempMatrix=new int[timeWindow][timeWindow][timeWindow];
		for(int i=0;i<timeWindow;i++)
		{
			for(int j=0;j<timeWindow;j++)
				for(int k=0;k<timeWindow;k++)
				{
					optimalMatrix[i][j][k]=-1;
					tempMatrix[i][j][k]=-1;
				}
		}
	}
	public OptimalWithoutSpot()
	{
		initialOptimalMatrix();
		for(int i=0;i<timeWindow;i++)
		{
			numberOfOndemand[i]=-1;
			numberOfReserved[i]=-1;
		}
	}
	public OptimalWithoutSpot( int[]numberReserved, int[] numberOndemand)
	{
		if(numberReserved.length<timeWindow || numberOndemand.length<timeWindow)
		{
			System.out.println("length of numberReserved array and numberOndemand is less than timewindow");
			System.exit(-1);
		}
		else
		{
			for(int i=0;i<timeWindow;i++)
			{
				numberOfOndemand[i]=numberOndemand[i];
				numberOfReserved[i]=numberReserved[i];
			}
		}
		for(int i=0;i<timeWindow;i++)
		{
			for(int j=0;j<timeWindow;j++)
				for(int k=0;k<timeWindow;k++)
				{
					optimalMatrix[i][j][k]=-1;
				}
		}
	}
	/**
	 * main algorithm return the optimal segmentation of capacity
	 */
	public void greedyAlgorithm(long onDemandArrival[], long reservedArrival[], double utilizationMean, double ondemandLifeMean )
	{
		double maxRevenue=-1;
		
		// call function to estimate the number reserved still available
		int numberAvailableReserved=getNumberAvailableOndemandInstance(0);
		//call function to estimate the number on demand still available
		int numberAvailableOndemand=getNumberAvailableOndemandInstance(0);
		
		for(int t=0;t<timeWindow;t++)
		{
			double revenue=0;
			long totalBeUsingInstance=numberAvailableOndemand+numberAvailableReserved;
			long availalbeCapacityforReserved=Math.min(CAPACITY-totalBeUsingInstance,reservedArrival[t]);
			
			for(int reservedAccepted =0; reservedAccepted < availalbeCapacityforReserved;reservedAccepted++)
			{
				//add the premium to revenue
				revenue=revenue+reservedAccepted*SystemProperties.RESOURCE_MANAGEMENT_RESERVED_PREMIUM.getValueAsDouble();
				//estimate the usage mean of reserved instance then add the cost of actived reserved instances
				long totalReservedInstance=numberAvailableReserved+reservedAccepted;
				double reservedUtilization=getUtilizationMeanOfReservedInsatance(t);
				revenue=revenue+reservedUtilization*totalReservedInstance*(reservedDiscount*ondemandPrice);
				//now checking the admission for on demand request
				long availableCapacityforOndemand=Math.min(availalbeCapacityforReserved, onDemandArrival[t]);
				for(int ondemandAccepted=0;ondemandAccepted < availableCapacityforOndemand;ondemandAccepted++)
				{
					//add the cost of on demand usage
					revenue=revenue+(numberAvailableOndemand+ondemandAccepted)*ondemandPrice;
				}
				
		
			}
		}
		
	}
	/**
	 * this method used to find the optimal segmentation over time window, it returns the maximum expected revenue and 
	 * the matrix keeps all the information about pricing model segmentation
	 * @param timeslot
	 * @param numberOndemandAvailable number on demand instances still available 
	 * @param numberReservedAvailable number reserved instances still available
	 * @param tempMatrix
	 * @return totalRevenue over time window
	 */
	public double recursive(int timeslot, long numberOndemandAvailable,long numberReservedAvailable, int[][][]tempMatrix)
	{
		if(timeslot<0 ||timeslot>timeWindow)
		{
			//initialOptimalMatrix();//set every element is matrix to -1
			return 0;
		}
		
		// call function to estimate the number reserved still available at time t
		int numberAvailableReserved=getNumberAvailableOndemandInstance(timeslot);
		//call function to estimate the number on demand still available
		int numberAvailableOndemand=getNumberAvailableOndemandInstance(timeslot);
		long totalBeUsingInstance=numberAvailableOndemand+numberAvailableReserved;
		long availalbeCapacityforReserved=Math.min(CAPACITY-totalBeUsingInstance,numberOfReserved[timeslot]);
		double totalRevenue=0;
		double revenue_at_t=0;
		for(int reservedAccepted =0; reservedAccepted < availalbeCapacityforReserved;reservedAccepted++)
		{
			//add the premium to revenue
			revenue_at_t=revenue_at_t+reservedAccepted*SystemProperties.RESOURCE_MANAGEMENT_RESERVED_PREMIUM.getValueAsDouble();
			//estimate the usage mean of reserved instance then add the cost of actived reserved instances
			long totalReservedInstance=numberAvailableReserved+reservedAccepted;
			double reservedUtilization=getUtilizationMeanOfReservedInsatance(timeslot);
			revenue_at_t=revenue_at_t+reservedUtilization*totalReservedInstance*(reservedDiscount*ondemandPrice);
			//now checking the admission for on demand request
			long availableCapacityforOndemand=Math.min(availalbeCapacityforReserved, numberOfOndemand[timeslot]);
			for(int ondemandAccepted=0;ondemandAccepted < availableCapacityforOndemand;ondemandAccepted++)
			{
				//add the cost of on demand usage
				revenue_at_t=revenue_at_t+(numberAvailableOndemand+ondemandAccepted)*ondemandPrice;
				tempMatrix[timeslot][0][0]=reservedAccepted;
				tempMatrix[timeslot][1][0]=ondemandAccepted;
				tempMatrix[timeslot][1][1]=100;
				//check if any on demand requests still wait for accepted
				long remainingOndemadRequest=numberOfOndemand[timeslot]-ondemandAccepted;
				if(remainingOndemadRequest>0)
				{
					//call function to estimate SLA violation and the overbooking ratio factor
					double slaviolation=getExpectedSLAViolation(timeslot);
					double overbookingRation=getOverbookingRatio(timeslot);
					revenue_at_t=revenue_at_t-slaviolation;
				}
				// call function to estimate the number reserved still available at time t+1
				int reservedAvailable_t1=getNumberAvailableOndemandInstance(timeslot+1);
				//call function to estimate the number on demand still available
				int ondemandAvailable_t1=getNumberAvailableOndemandInstance(timeslot);
				
				totalRevenue= revenue_at_t+ recursive(timeslot+1,ondemandAvailable_t1,reservedAvailable_t1,tempMatrix);
				
				if( (timeslot==timeWindow-1) && (totalRevenue>maxiRevenue))
				{
					maxiRevenue=totalRevenue;
					optimalMatrix=tempMatrix;	
				}
			}
			
		}
		return totalRevenue;
	}
	/**
	 * 
	 * @return
	 */
	public double getUtilizationMeanOfReservedInsatance(long timeslot)
	{
		
		double utl=-1;
		double meanUsage=0;
		ArrayList<ReservedRequest>reservedInstance=getReservedInstance(timeslot);
		for(ReservedRequest r:reservedInstance)
		{
			meanUsage+=r.getDuration();
		}
		meanUsage=meanUsage/(reservedInstance.size());
		//TODO
		return utl;
	}
	public ArrayList<ReservedRequest> getReservedInstance(long timeslot)
	{
		ArrayList<ReservedRequest> reservedInstanceList=new ArrayList<ReservedRequest>();
		
		//
		return reservedInstanceList;
	}
	public int getNumberAvailableReservedInstance(long timeslot)
	{
		int noAvailableReserved=0;
		//TODO 
		return noAvailableReserved;
	}
	public int getNumberAvailableOndemandInstance(long timeslot)
	{
		int noAvailableOndemand=0;
		//TODO
		return noAvailableOndemand;
	}
	public double getExpectedSLAViolation(long timeslot)
	{
		double slaCompensation=SystemProperties.RESOURCE_MANAGEMENT_SLAVIOLATION_COMPENSATION.getValueAsDouble();//this is the compensation ratio
		
		//TODO
		return 0;
	}
	/**
	 * used to estimate sla violation happen for each users at a specific timeslot.
	 * it estimates total resource usage of each user and the percentage of unavailability of resource happen to them.
	 *  idea: loop in user contracts and current resource usage
	 * @param timeslot
	 * @return
	 */
	public double estimateSLAviolationPerUser(long timeslot)
	{
		//TODO
		return 0;
	}
	/**
	 * estimate the overbooking threshold at timeslot t. Objective is to minimize sla violation
	 * @param timeslot the time slot t which need to calculate the overbook ratio
	 * @return ratio
	 */
	public double getOverbookingRatio(long timeslot)
	{
		double ratio=-1;
		for(int i=0;i<getTimeWindow();i++)
		{
			double threshold=getOverbookingRatio(i);
			if(threshold> this.overbooking_threshold)
			{
				ratio=overbooking_threshold;
				
			}
			else
			{
				ratio=threshold;
			}
		}
		//TODO
		return ratio;
	}
	

	int getTimeWindow() {
		return timeWindow;
	}

	


	private long[] getNumberOfReserved() {
		return numberOfReserved;
	}



	private void setNumberOfReserved(long[] numberOfReserved) {
		this.numberOfReserved = numberOfReserved;
	}



	private long[] getNumberOfOndemand() {
		return numberOfOndemand;
	}



	private void setNumberOfOndemand(long[] numberOfOndemand) {
		this.numberOfOndemand = numberOfOndemand;
	}

}
