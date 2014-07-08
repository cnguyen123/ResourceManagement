package org.cloudbus.cloudsim.requestCatergory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.cloudbus.cloudsim.workloadUtil.VirtualInstance;

public class RequestClassify {
	private final String FILENAME="data/vm_user.txt";
	ArrayList<OndemandRequest>ondemand;
	ArrayList<ReservedRequest>reserved;
	ArrayList<SpotRequest>spot;
	private ArrayList<Request>requestList;
	public RequestClassify()
	{
		this.ondemand=new ArrayList<OndemandRequest>();
		this.reserved=new ArrayList<ReservedRequest>();
		this.spot=new ArrayList<SpotRequest>();
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
			switch(request.getType())
			{
			case ONDEMAND: OndemandRequest ondemandRequest=(OndemandRequest)request;
					ondemand.add(ondemandRequest);
				break;
			case SPOT: SpotRequest spotRequest=(SpotRequest)request;
						spot.add(spotRequest);
			break;
			case RESERVED: ReservedRequest reservedRequest=(ReservedRequest)request;
							reserved.add(reservedRequest);
				break;
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
	private void gaussanClassify()
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
			long duration=(re.getDuration()-minDuration)/(maxDuration-minDuration);
			if((duration>0 && duration<=0.25)|| (duration ==0))
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
				try{
					File file=new File(FILENAME);
					@SuppressWarnings("resource")
					BufferedReader breader=new BufferedReader(new InputStreamReader(new FileInputStream(file)));
					int requestId=0;
					while(breader.ready())
					{
						String line=breader.readLine();
						int userId=Integer.parseInt(line.split(" ")[0]);
						long initialTime=Long.parseLong(line.split(" ")[1]);
						long endedTime=Long.parseLong(line.split(" ")[2]);
						int numberInstance=Integer.parseInt(line.split(" ")[3]);
						Request request=new Request(requestId);
						request.setNumberInstance(numberInstance);
						request.setUserId(userId);
						request.setInitialTime(initialTime);
						request.setEndTime(endedTime);
						requestList.add(request);
					}
					
					
				}
				catch(Exception ex){
					System.out.println(ex.getMessage());
				}
	}
	private void writeRequestToFile()
	{
		try{
			System.out.println("Writing the ondemand requests to file...");
			File ondemandFile=new File("data/request/OndemandRequest.txt");
			BufferedWriter bwriter1=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ondemandFile)));
			for(OndemandRequest on: this.ondemand)
			{
				bwriter1.write(on.getRequestId()+" "+on.getUserId()+" "+on.getInitialTime()+" "+on.getEndTime()+" "+on.getNumberInstance());
				bwriter1.write("\n");
			}
			bwriter1.close();
			//
			System.out.println("Writing the spot requests to file...");
			File spotFile=new File("data/request/SpotRequest.txt");
			BufferedWriter bwriter2=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(spotFile)));
			for(SpotRequest sp: this.spot)
			{
				bwriter2.write(sp.getRequestId()+" "+sp.getUserId()+" "+sp.getInitialTime()+" "+sp.getEndTime()+" "+sp.getNumberInstance());
				bwriter2.write("\n");
			}
			bwriter2.close();
			//
			System.out.println("Writing the reserved requests to file...");
			File reservedFile=new File("data/request/ReservedRequest.txt");
			BufferedWriter bwriter3=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(reservedFile)));
			for(SpotRequest sp: this.spot)
			{
				bwriter3.write(sp.getRequestId()+" "+sp.getUserId()+" "+sp.getInitialTime()+" "+sp.getEndTime()+" "+sp.getNumberInstance());
				bwriter3.write("\n");
			}
			bwriter3.close();
			System.out.println("Finishing writing to file!");
			
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}
	}
	public void classify()
	{
		gaussanClassify();
		writeRequestToFile();
	}
	public static void main(String args[])
	{

	
	
		RequestClassify classify=new RequestClassify();
		classify.classify();
	}
	

}
