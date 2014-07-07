package org.cloudbus.cloudsim.workloadUtil;

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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * this class is used to read and create Virtual instance list from file given 
 * @author lnguyen2
 *
 */
public class VirtualInstanceGenerator {
	private Set<Integer> userIdSet;
	private final File file;
	private final int MAX_FIELD=3;// this is maximum number of fields in file keeping google trace vm -- userid---startedtime---endedtime
	private final int USERID_INDEX=0;//index of userid in a line
	private final int STARTEDTIME_INDEX=1;//index of started time in a line
	private final int ENDEDTIME_INDEX=2;// index of ended time in a line
	private final String FILEOUTPUT="data/output.txt";
	private ArrayList<VirtualInstance> vmList;
	private String []fieldArray=null;
	private ArrayList<String>vmcreated=null;
	public VirtualInstanceGenerator(String fileName) throws FileNotFoundException
	{
		if (fileName == null || fileName.length() == 0) {
			throw new IllegalArgumentException("Invalid trace file name.");
		} 

		file = new File(fileName);
		if (!file.exists()) {
			throw new FileNotFoundException("Workload trace " + fileName + " does not exist");
		}
	
	}
	public void vmGenerate()
	{
		if (vmList==null){
			vmList=new ArrayList<VirtualInstance>();
			setUserIdSet(new HashSet<Integer>());
			fieldArray=new String[MAX_FIELD];
			
		}
		try {
			if (file.getName().endsWith(".gz")) {
				readGZIPFile(file);
			} else if (file.getName().endsWith(".zip")) {
				readZipFile(file);
			} else {
				readFile(file);
				
			}
		} catch (final FileNotFoundException e) {
			System.out.println("File not found error! Can not find the file "+file.getName());
		} catch (final IOException e) {
			System.out.println("IO error!");
		}
	}
	public void readGZIPFile(File file)throws FileNotFoundException
	{
		//TODO
	}
	public void readZipFile(File file) throws FileNotFoundException
	{
		//TODO
	}
	public boolean readFile(final File file) throws FileNotFoundException,IOException
	{
		boolean success = false;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

			// read one line at the time
			int line = 1;
			while (reader.ready()) {
				parseValue(reader.readLine(), line);
				line++;
			}

			reader.close();
			success = true;
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		return success;
	}
	
	private void parseValue(final String line, final int lineNum) {
		

		final String[] sp = line.split(","); // split the fields based on a
		// space
		int len = 0; // length of a string
		int index = 0; // the index of an array

		// check for each field in the array
		for (final String elem : sp) {
			len = elem.length(); // get the length of a string

			// if it is empty then ignore
			if (len == 0) {
				continue;
			}
			fieldArray[index] = elem;
			index++;
		}
		if (index == MAX_FIELD) {
			extractField(fieldArray, lineNum);
		}
	
	}
	private void extractField(final String[] array, final int line) {
		try {
			

			// get the started time
			final Long started = new Long(array[STARTEDTIME_INDEX].trim());
			final long startedTime = started.longValue();

			// get the ended time
			final Long ended = new Long(array[ENDEDTIME_INDEX].trim());
			final long endedTime = ended.longValue();

			final int userID = new Integer(array[USERID_INDEX].trim()).intValue();
			this.getUserIdSet().add(userID);
			
			createVm(userID, startedTime, endedTime);
		} catch (final Exception e) {
			System.out.println(e.getMessage());

		}
	}
	private void createVm(int userId, long startedTime, long endedTime)
	{
		if(startedTime<0)
		{
			System.out.println("<0");
		}
		VirtualInstance vm=new VirtualInstance(userId, startedTime, endedTime);
		
		//only get those vms have usage time more than 1000 seconds
		if(vm.getUsageTimeInSecond()>=1000)
		vmList.add(vm);
	}
	public void backupVm()
	{
		try{
			File out=new File(FILEOUTPUT);
			BufferedWriter bwriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out)));
			for(VirtualInstance vm: vmList)
			{
				String line=vm.getUserId()+" "+vm.getStartedTimeInSecond()+" "+vm.getEndedTimeInSecond()+"\n";
				bwriter.write(line);
			}
			bwriter.close();
			
		}
		catch(FileNotFoundException ex)
		{
			System.out.println(ex.toString());
		}
		catch(IOException ex)
		{
			System.out.println(ex.toString());
		}
	}
	public ArrayList<VirtualInstance> getVmList() {
		return vmList;
	}

	public void setVmList(ArrayList<VirtualInstance> vmList) {
		this.vmList = vmList;
	}
	/**
	 * group virtual machines have been created by its user and sort them by started time ascending 
	 */
	private void groupVmbyUserId()
	{
		//ascending sort by the ended time
		Collections.sort(this.getVmList(), new Comparator<VirtualInstance>() {

			@Override
			public int compare(VirtualInstance o1, VirtualInstance o2) {
				return new Long(o1.getEndedTime()).compareTo(o2.getEndedTime());
				
			}
			
		});
		vmcreated=new ArrayList<String>();
		
		HashMap<UserAndInstance, Integer>vmcreated=new HashMap<UserAndInstance, Integer>();
		for(VirtualInstance vi: getVmList())
		{
			UserAndInstance user=new UserAndInstance(vi.getUserId(), vi.getStartedTimeInSecond(), vi.getEndedTimeInSecond());
			
			if(!vmcreated.isEmpty()&& vmcreated.containsKey(user))
			{
				
				int increasing=vmcreated.get(user)+1;
				
				vmcreated.remove(user);
				vmcreated.put(user, increasing);
			}else
			{
				
				
				vmcreated.put(user, 1);
			}
		}
		for(Map.Entry<UserAndInstance, Integer>entry: vmcreated.entrySet())
		{
			int numberVm=entry.getValue();
			UserAndInstance uai=entry.getKey();
			int userid=uai.getUserId();
			long startedTime=uai.getStartedTime();
			long endedTime=uai.getEndedTime();
			this.vmcreated.add(userid+" "+startedTime+" "+endedTime+" "+ numberVm);
			//System.out.println(userid+" "+startedTime+" "+endedTime+" "+ numberVm);
			
		}
		
	}
	public void writeToFile(String fileName)
	{
		groupVmbyUserId();
		try{
			File output=new File(fileName);
			BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output)));
			for(String t:this.vmcreated)
			{
				writer.write(t+"\n");
			}
			
			writer.close();
		}
		catch(FileNotFoundException ex)
		{
			System.out.println(ex.getMessage());
		}
		catch(IOException iex)
		{
			System.out.println(iex.getMessage());
		}
		
		
		
	}
	public Set<Integer> getUserIdSet() {
		return userIdSet;
	}
	private void setUserIdSet(Set<Integer> userSet) {
		this.userIdSet = userSet;
	}


}