package org.cloudbus.cloudsim.systemConfig;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * This class is the bridge to connect to the file configuration and the class enum SystemProperties.
 * From that building values for properties based on the configuration specified in file configuration 
 * 
 * @author lnguyen2
 *
 */
public class SystemConfiguration {
	public static final String FILE_CONF="conf/system.properties";
	private Properties properties;
	/**
	 * this attribute used to define whether the SystemConfiguration instance has been created or not
	 */
	private static SystemConfiguration singleton;
	/**
	 * load default properties specified in file FILE_CONF and keeps them in the properties attribute of 
	 * SystemConfiguration instance
	 */
	private void loadDefaultProperties()
	{
		Properties defaultProperties=new Properties();
		try {
			InputStream in =new FileInputStream(FILE_CONF);
			defaultProperties.load(in);
			in.close();
		} catch (IOException ex) {
			//Log.logger.warning("Cannot open default properties file");
			System.exit(2);
		}catch(Exception e){
			e.printStackTrace();
		}
		this.properties=(new Properties(defaultProperties));
	}

	protected String getProperty(String key)
	{
		synchronized (this.properties) {
			return this.properties.getProperty(key);
			
		}
	
	}
	protected Object setProperties(String key, String value)
	{
		synchronized (this.properties) {
			return(this.properties.setProperty(key, value));
			
		}
	}
	/**
	 * 
	 * @return singleton the initiated SystemConfiguration object
	 */
	public static SystemConfiguration getInstance()
	{
		if(singleton==null){
			singleton =new SystemConfiguration();
		}
		return singleton;
	}
	/**
	 * this class creator will be called at the first time to initial new SystemConfiguration object
	 */
	private SystemConfiguration()
	{
		loadDefaultProperties();
	}

}
