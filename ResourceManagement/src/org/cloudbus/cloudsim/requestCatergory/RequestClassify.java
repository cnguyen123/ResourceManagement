package org.cloudbus.cloudsim.requestCatergory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import org.cloudbus.cloudsim.systemConfig.SystemProperties;






public class RequestClassify {
	private final int seed=111085;
	private final String FILENAME="data/vm_user.txt";
	private ArrayList<OndemandRequest>ondemand;
	private ArrayList<ReservedRequest>reserved;
	private ArrayList<SpotRequest>spot;
	private ArrayList<Request>requestList;
	private final long reservedLengthInSecond=SystemProperties.RESOURCE_MANAGEMENT_RESERVED_LENGTH.getValueAsInt()*3600;// in second
	//
	ArrayList<ReservationWorkload>list;
	public RequestClassify()
	{
		this.setOndemand(new ArrayList<OndemandRequest>());
		this.setReserved(new ArrayList<ReservedRequest>());
		this.setSpot(new ArrayList<SpotRequest>());
		this.setRequestList(new ArrayList<Request>());
		
	}
	/**
	 * this function used to add new requests to correct list based on its type
	 * @param nw new request which will be added to specific list of workload
	 */
	private void addNewWorkloadtoList(Request request)
	{
		try
		{
			if(request.getType()==null){
				System.out.println("NULL");
			}
			else
			{
				int requestId=request.getRequestId();
				int userId=request.getUserId();
				int numberInstance=request.getNumberInstance();
				RequestType type=request.getType();
				long initialTime=request.getInitialTime();
				long endTime=request.getEndTime();
				
				switch(type)
				{
				case ONDEMAND: OndemandRequest ondemandRequest= new OndemandRequest(requestId);
								ondemandRequest.setUserId(userId);
								ondemandRequest.setInitialTime(initialTime);
								ondemandRequest.setEndTime(endTime);
								ondemandRequest.setNumberInstance(numberInstance);
						getOndemand().add(ondemandRequest);
					break;
				case SPOT: SpotRequest spotRequest=new SpotRequest(requestId);
							spotRequest.setUserId(userId);
							spotRequest.setInitialTime(initialTime);
							spotRequest.setEndTime(endTime);
							spotRequest.setNumberInstance(numberInstance);
							getSpot().add(spotRequest);
				break;
				case RESERVED: ReservedRequest reservedRequest=new ReservedRequest(requestId);
								reservedRequest.setUserId(userId);
								reservedRequest.setInitialTime(initialTime);
								reservedRequest.setEndTime(endTime);
								reservedRequest.setNumberInstance(numberInstance);
								getReserved().add(reservedRequest);
					break;
				}
			}
			
		}
		catch(ClassCastException castEx)
		{
			System.out.println(castEx.getMessage());
		}
		
	}
	/**
	 * this function used to classify the request in dataset into specific request domain, say: ondemand, reserved, and spot 
	 */
	public void gaussanClassify()
	{
		//call function to create requestlist
		createRequestList();
		//first normalize the lifetime of each workload so that its lifetime is in (0,1)
		//sort the requestList in ascending order of the lifetime of VM
		Collections.sort(this.getRequestList(), new Comparator<Request>() {
			@Override
			public int compare(Request o1, Request o2) {
				
				Long o1LifeTime=new Long(o1.getDuration());
				
				Long o2LifeTime=new Long(o2.getDuration()); 
				return(o1LifeTime.compareTo(o2LifeTime));
				
			}
			
		});
		
		long minDuration=this.getRequestList().get(0).getDuration();
		long maxDuration=this.getRequestList().get(this.getRequestList().size()-1).getDuration();
		
		// second, classify the requests 
		for(Request re: this.getRequestList())
		{
			double duration=(double)(re.getDuration()-minDuration)/(maxDuration-minDuration);
			if(duration>=0 && duration<=0.25)
			{
				re.setType(RequestType.SPOT);
			}else if(duration>0.25 && duration<=0.5){
				re.setType(RequestType.ONDEMAND);
			}else if(duration>0.5 && duration<=1)
			{
				re.setType(RequestType.RESERVED);
			}
			addNewWorkloadtoList(re);
			
		}
		
	}
	ArrayList<Request> getRequestList() {
		return requestList;
	}
	void setRequestList(ArrayList<Request> requestList) {
		this.requestList = requestList;
	}
	private void createRequestList()
	{
		requestList=new ArrayList<Request>();
		//read workload from file
		/**
		 * notes: file keeps workloads information with the initial time and endtime in second. In that, 
		 * google trace only keeps 29 day workload.
		 * therefore, need to multiple with 12 to generate workloads inm a year. 
		 */
				try{
					File file=new File(FILENAME);
					@SuppressWarnings("resource")
					BufferedReader breader=new BufferedReader(new InputStreamReader(new FileInputStream(file)));
					int requestId=0;
					while(breader.ready())
					{
						String line=breader.readLine();
						int userId=Integer.parseInt(line.split(" ")[0]);
						long initialTime=Long.parseLong(line.split(" ")[1]);//this is in second
						long endedTime=Long.parseLong(line.split(" ")[2]);//this is in second
						//multiple with 12 to scale it in a year
						initialTime=initialTime*12;
						endedTime=endedTime*12;
						int numberInstance=Integer.parseInt(line.split(" ")[3]);
						Request request=new Request(requestId);
						request.setNumberInstance(numberInstance);
						request.setUserId(userId);
						request.setInitialTime(initialTime);
						request.setEndTime(endedTime);
						request.updateDuration();
						requestList.add(request);
						requestId++; 
					}
					
					
				}
				catch(Exception ex){
					System.out.println(ex.getMessage());
				}
				//sort the request list ascending order by the request duration
				Collections.sort(this.requestList,new Comparator<Request>() {

					@Override
					public int compare(Request o1, Request o2) {
						Long o1duration=new Long(o1.getDuration());
						Long o2duration=new Long(o2.getDuration());
						return o1duration.compareTo(o2duration);
					}
				});
				
	}
	/**
	 * this function used to classify the requests based on its duration following the Gaussan distribution
	 */
	private void workloadClassify()
	{
		createRequestList();
		Random rd=new Random(seed);
		double reservedMean=0.75;
		double ondemandMean=0.5;
		double spotMean=0.25;
		double std=0.3;
		int n=this.requestList.size();
		int reservedCnt=17000;
		int spotCnt=120000;
		SortedMap<Double, RequestType> sm=new TreeMap<Double, RequestType>();
		int k=0;
		RequestType type=RequestType.ONDEMAND;
		while(k<n)
		{
			double mean=0;
			RequestType typeCur=type;
			if(k<=reservedCnt)
			{
				mean=reservedMean;
				type=RequestType.RESERVED;
			}else if(k>reservedCnt && k<=(reservedCnt+spotCnt)){
				mean=spotMean;
				type=RequestType.SPOT;
			}else{
				mean=ondemandMean;
				type=RequestType.ONDEMAND;
			}
			double key=rd.nextGaussian()*std+mean;
			while(sm.get(key)!=null ||key<0||key>1)
			{
				key=rd.nextGaussian()*std+mean;
			}
			k++;
			sm.put(key, typeCur);
		}
		// Mapping
		int i=0;
		for(RequestType t: sm.values())
		{
			requestList.get(i).setType(t);
			addNewWorkloadtoList(requestList.get(i));
			i++;
			
		}
		//split those reserved request which have the duration greater than reservation length (in the simulation is 1 year)
		splitRequestOutLength();
		
		//// resort list based on the initial time
		
		Collections.sort(getReserved(), new Comparator<ReservedRequest>() {
			@Override
			public int compare(ReservedRequest e1, ReservedRequest e2) {
				return new Long(e1.getInitialTime()).compareTo(new Long(e2
						.getInitialTime())); // sort based on the start time
			}
		});
		Collections.sort(getOndemand(), new Comparator<OndemandRequest>() {
			@Override
			public int compare(OndemandRequest e1, OndemandRequest e2) {
				return new Long(e1.getInitialTime()).compareTo(new Long(e2
						.getInitialTime())); // sort based on the start time
			}
		});
		Collections.sort(getSpot(), new Comparator<SpotRequest>() {
			@Override
			public int compare(SpotRequest e1, SpotRequest e2) {
				return new Long(e1.getInitialTime()).compareTo(new Long(e2
						.getInitialTime())); // sort based on the start time
			}
		});
		this.list=generateReservation();
		Collections.sort(this.list, new Comparator<ReservationWorkload>() {
			@Override
			public int compare(ReservationWorkload r1,ReservationWorkload r2)
			{
				return new Long(r1.getStartTime()).compareTo(new Long(r2.getStartTime()));
			}
		});
		
	}
	//
	/**
	 * this class used to split request in Reserved Request List into smaller request if its duration is greater than
	 * the reserved length specified by simulation
	 */
	private void splitRequestOutLength()
	{
		System.out.println("There are some requests in Reserved request have duration greater than the Reservation length.");
		System.out.println("Need to split it into smaller reserved requests");
		Iterator<ReservedRequest> ite=getReserved().iterator();
		ArrayList<ReservedRequest>newrequestList=new ArrayList<ReservedRequest>();
		int requestId=getReserved().size();
		while(ite.hasNext())
		{
			ReservedRequest reservedRequest=ite.next();
			long duration=reservedRequest.getDuration();
			if(duration>reservedLengthInSecond)
			{
				long initialTime=reservedRequest.getInitialTime();
				int numberInstance=reservedRequest.getNumberInstance();
				int userId=reservedRequest.getUserId();
				ite.remove();
				//loop to break the request until its duration is less than reserved length
				while(duration>reservedLengthInSecond)
				{
					long endTime=initialTime+reservedLengthInSecond-1;
					ReservedRequest brokenReservedRequest=new ReservedRequest(requestId, initialTime, endTime, 0, RequestType.RESERVED); 
					brokenReservedRequest.setNumberInstance(numberInstance);
					brokenReservedRequest.setUserId(userId);
					newrequestList.add(brokenReservedRequest);
					duration -= reservedLengthInSecond - 1;
					initialTime= initialTime + reservedLengthInSecond;
					requestId++;
				}
				//after looping, check the remaining duration whether it is greater than zero
				if (duration > 0) {
					ReservedRequest brokenReservedRequest=new ReservedRequest(requestId,initialTime,initialTime+duration,0,RequestType.RESERVED);
					brokenReservedRequest.setNumberInstance(numberInstance);
					newrequestList.add(brokenReservedRequest);
					
				}
			}
		}
		// now add the list of broken reserved request to the list reserved request
		for(ReservedRequest reservedRequest: newrequestList)
		{
			getReserved().add(reservedRequest);
		}
		
	}
	private void writeRequestToFile(FileOutput out) throws FileNotFoundException,IOException
	{
		switch(out)		
		{
		case ONDEMAND:
			System.out.println("Writing the ondemand requests to file...");
			File ondemandFile=new File("data/request/OndemandRequest.txt");
			BufferedWriter bwriter1=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ondemandFile)));
		
			for(OndemandRequest on: this.getOndemand())
			{
			bwriter1.write(on.getRequestId()+" "+on.getUserId()+" "+on.getInitialTime()+" "+on.getEndTime()+" "+on.getNumberInstance());
			bwriter1.write("\n");
			}
			bwriter1.close();
			break;
		
		case RESERVED:
		{
			System.out.println("Writing the reserved requests to file...");
			File reservedFile=new File("data/request/ReservedRequest.txt");
			BufferedWriter bwriter3=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(reservedFile)));
			for(ReservedRequest rs: this.getReserved())
			{
				bwriter3.write(rs.getRequestId()+" "+rs.getUserId()+" "+rs.getInitialTime()+" "+rs.getEndTime()+" "+rs.getNumberInstance());
				bwriter3.write("\n");
			}
			bwriter3.close();
			break;
		}
		case SPOT:
		{
			System.out.println("Writing the spot requests to file...");
			File spotFile=new File("data/request/SpotRequest.txt");
			BufferedWriter bwriter2=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(spotFile)));
			for(SpotRequest sp: this.getSpot())
			{
			
				bwriter2.write(sp.getRequestId()+" "+sp.getUserId()+" "+sp.getInitialTime()+" "+sp.getEndTime()+" "+sp.getNumberInstance());
				bwriter2.write("\n");
			}
			bwriter2.close();
			
			break;
		}
		case RESERVATIONWORKLOAD:
			System.out.println("Writing the Reservation workload to file...");
			File reworkloadFile=new File("data/request/reservationworkload.txt");
			BufferedWriter bwriter2=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(reworkloadFile)));
			for(ReservationWorkload re: list)
			{
			
				bwriter2.write(re.getUserId()+" "+re.getVms()+" "+re.getStartTime()+" "+re.getEndTime());
				bwriter2.write("\n");
			}
			bwriter2.close();
			
			break;
			
			
		}
		
	}
	
	public void doClassify()
	{
		workloadClassify();
		try{
			writeRequestToFile(FileOutput.ONDEMAND);
			writeRequestToFile(FileOutput.RESERVED);
			writeRequestToFile(FileOutput.SPOT);
			//
			writeRequestToFile(FileOutput.RESERVATIONWORKLOAD);
		}
		catch(FileNotFoundException fnfex)
		{
			System.out.println(fnfex.getMessage());
		}
		catch(IOException ioex)
		{
			System.out.println(ioex.getMessage());
		}
	
		
	}
	
	public static void main(String args[])
	{

		
		RequestClassify classify=new RequestClassify();
		classify.doClassify();
	}
	ArrayList<ReservedRequest> getReserved() {
		return reserved;
	}
	void setReserved(ArrayList<ReservedRequest> reserved) {
		this.reserved = reserved;
	}
	ArrayList<SpotRequest> getSpot() {
		return spot;
	}
	void setSpot(ArrayList<SpotRequest> spot) {
		this.spot = spot;
	}
	ArrayList<OndemandRequest> getOndemand() {
		return ondemand;
	}
	void setOndemand(ArrayList<OndemandRequest> ondemand) {
		this.ondemand = ondemand;
	}
	/**
	 * calculate the standard deviation of request list duration
	 * @param type the type of Request(reserved, ondemand, or spot)
	 * @return
	 */
	public double getSTDofDuration(RequestType type)
	{
		long temp = 0;
		int cnt = 0;
		double mean = getMeanofDuration(type);
		switch (type) {
		case ONDEMAND:
			for (OndemandRequest on : getOndemand()) {
				long duration = on.getDuration();
				temp += (mean - duration) * (mean - duration);
				cnt++;
			}
			break;
		case SPOT:
			for (SpotRequest sp : getSpot()) {
			long duration = sp.getDuration();
			temp += (mean - duration) * (mean - duration);
			cnt++;
		}
			
			break;
		case RESERVED:
			for (ReservedRequest re : getReserved()) {
				long duration = re.getDuration();
				temp += (mean - duration) * (mean - duration);
				cnt++;
			}
			break;
		}
		if (cnt > 0)
			return Math.sqrt(temp / (double) cnt);

		return -1;
	}
	/**
	 * calculate the mean of duration of request list
	 * @param type the type of request(reserved, ondemand, or spot)
	 * @return
	 */
	public double getMeanofDuration(RequestType type)
	{
		long sum = 0;
		int cnt = 0;
		
		switch (type) {
		case ONDEMAND:
			for(OndemandRequest on: getOndemand())
			{
				long duration=on.getDuration();
				sum+=duration;
				cnt++;
			}
			break;
		case SPOT:
			for(SpotRequest on: getSpot())
			{
				long duration=on.getDuration();
				sum+=duration;
				cnt++;
			}
			break;
		case RESERVED:
			for(ReservedRequest on: getReserved())
			{
				long duration=on.getDuration();
				sum+=duration;
				cnt++;
			}
			break;
		}

		if (cnt > 0)
			return sum / (double) cnt;

		return -1;
	}
	/**
	 * this function used to create reservation workload based on the reserved contract. It follows the lazy policy
	 * more specifically, every reserved requests of each user arrive, this function check this user's reserved capacity 
	 * still has available vm instances suitable to host this task or not. If there is suitalbe vm avaialbe, the job will be
	 * host on that Vm, otherwise, user has to request new reserved contract with provider with number instance needed to 
	 * host the job.
	 * @return list of reservation workload
	 */
	public ArrayList<ReservationWorkload> generateReservation()
	{
		HashMap<Integer, ArrayList<ReservedRequest>> users=new HashMap<Integer, ArrayList<ReservedRequest>>();
		for(ReservedRequest reservedRequest:getReserved())
		{
			ArrayList<ReservedRequest>list=users.get(reservedRequest.getUserId());
			//if the hashmap hasn't keep userid then we initial list request for that userid
			if(list==null)
			{
				list=new ArrayList<ReservedRequest>();
			}
			//add all reserved jobs which are jobs waiting for running on reserved instance belong to this user to list
				list.add(reservedRequest);
				users.put(reservedRequest.getUserId(), list);
		}
		//this is the list of all reserved instance request will be sent to provider to create new reserved instances
		ArrayList<ReservationWorkload> reservationWorkloadList=new ArrayList<ReservationWorkload>();
		for (int i : users.keySet()) {
			ArrayList<ReservedRequest> reservedListOfSpecificUser = users.get(i);
			//each user will have one listPair represents the list of all reserved requests from this user
			ArrayList<Pair> listPair = new ArrayList<RequestClassify.Pair>();
			listPair.add(new Pair(-1, 0));
			listPair.add(new Pair(Long.MAX_VALUE, 0));
			
			for (ReservedRequest re : reservedListOfSpecificUser) {
				int l = getJobMaxNeed(listPair, re);
				if (l > 0) {
					//if the program come into this, means that it needs to create new reserved request because the user's reserved instance capacity
					//is not enough for hosting this job
					ReservationWorkload res = new ReservationWorkload(
							re.getUserId(), l, re.getInitialTime(),
							re.getInitialTime() + reservedLengthInSecond);
					reservationWorkloadList.add(res);
					update(listPair, res.getStartTime(), res.getEndTime(),
							res.getVms());
				}
				update(listPair, re.getInitialTime(), re.getEndTime(), -re.getNumberInstance());

			} // end of for jobs
		} // end of for users
		return reservationWorkloadList;
	}
	private static void update(ArrayList<Pair> list, long start, long end,
			int size) {
		int i = 0;
		//this loop in the pair list to get the exact index of position the new pair will be inserted
		for (i = 0; list.get(i).time < start; i++)
			;
		//check if there exists a pair in list that has time equal to started time of reserved instance
		if (list.get(i).time == start)
			list.get(i).available += size;
		else {
			//otherwise create new Pair and add in that index
			Pair pair = new RequestClassify.Pair(start,
					list.get(i - 1).available + size);
			list.add(i, pair);
		}

		i++;
		//update those Pairs which have time less than end Time of new reserved instance
		for (; list.get(i).time < end; i++)
			list.get(i).available += size;

		if (list.get(i).time != end) {
			Pair pair = new Pair(end, list.get(i - 1).available - size);
			list.add(i, pair);
		}

	}
	/**
	 * 
	 * @param list list of Pair represents the reserved requests that job owner has requested provider so far
	 * @param job specific job which needs to get max needed
	 * @return
	 */
	private static int getJobMaxNeed(ArrayList<Pair> list, ReservedRequest job) {

		int s = 0;
		for (int i = 0; i < list.size(); i++) {
			Pair pair = list.get(i);
			if (pair.time >= job.getInitialTime()) {
				if (pair.time == job.getInitialTime())
					s = i;
				else
					s = i - 1;
				break;
			}
		}

		//
		int k = 0;
		Iterator<Pair> itr = list.iterator();
		while (itr.hasNext() && k < s) {
			itr.next();
			itr.remove();
			k++;
		}

		int max = 0;
		for (int i = 0; i < list.size(); i++) {
			Pair pair = list.get(i);
			//if the job end time is greater than this reserved instance contract life
			//then get the
			if (pair.time < job.getEndTime()) { 
				int need = job.getNumberInstance() - pair.available;
				if (need > max)
					max = need;
			} else {
				break;
			}
		}

		return max;
	}
	
	/**
	 * enumeration of File output. It includes on demand file, reserved file, spot request file and the reservation workload file
	 * @author lnguyen2
	 *
	 */
	public enum FileOutput{
		ONDEMAND,RESERVED,SPOT,RESERVATIONWORKLOAD
		
	}
	
	public static class Pair {
		@Override
		public String toString() {
			return "Pair [time=" + time + ", available=" + available + "]";
		}

		public Pair(long time, int available) {
			super();
			this.time = time;
			this.available = available;
		}

		public long time;
		public int available;
	};
	

}
