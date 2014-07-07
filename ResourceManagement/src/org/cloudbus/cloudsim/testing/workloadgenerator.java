package org.cloudbus.cloudsim.testing;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import org.cloudbus.cloudsim.workloadUtil.User;
import org.cloudbus.cloudsim.workloadUtil.UserAndInstance;
import org.cloudbus.cloudsim.workloadUtil.VirtualInstance;
import org.cloudbus.cloudsim.workloadUtil.VirtualInstanceGenerator;

public class workloadgenerator {
	public static void main(String arg[])
	{
		try{
			String fileName="data/vmrequests.csv";
			VirtualInstanceGenerator viGenerator=new VirtualInstanceGenerator(fileName);
			viGenerator.vmGenerate();
			viGenerator.writeToFile("data/vm_user.txt");
			
			
		}
		catch(FileNotFoundException ex)
		{
			System.out.println("File not found error!");
		}
		
	}

}
