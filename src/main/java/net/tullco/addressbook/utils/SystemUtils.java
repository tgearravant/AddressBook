package net.tullco.addressbook.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import net.tullco.addressbook.App;

public class SystemUtils {
	final private static Boolean IS_WINDOWS=(System.getProperty("os.name").contains("Windows"));
	final private static String[] requiredProperties={"admin_username","admin_password","s3_access_key_id","s3_secret_key","backup_key"};
	private static Properties properties=null;
	/**
	 * Takes a Unix type path and, if it detects a Windows operating system, converts it to Windows.
	 * @param path A Unix path
	 * @return Returns a path string appropriate for the OS.
	 */
	public static String adjustPathForOS(String path){
		if (IS_WINDOWS){
			return path.replace('/', '\\');
		}
		else
			return path;
	}
	public static boolean inProduction(){
		return !IS_WINDOWS;
	}
	private static Properties loadProperties(Properties p,String s){
		if (SystemUtils.properties!=null)
			return SystemUtils.properties;
		InputStream input=null;
		try{
			String filename="config/"+s;
			input=App.class.getClassLoader().getResourceAsStream(filename);
			if (input==null) {
				System.out.println("Could not load properties...");
				return p;
			}
			p.load(input);
		}catch (IOException e){
			e.printStackTrace();
		}finally{
			try{
				input.close();
			}catch(IOException e){}catch(NullPointerException e){}
		}
		return p;
	}
	private static void loadPropertiesWithDefaults(){
		Properties defaultProps = new Properties();
		loadProperties(defaultProps,"config.properties.default");
		Properties p=new Properties(defaultProps);
		loadProperties(p,"config.properties");
		SystemUtils.properties=p;
	}
	public static String getProperty(String s,String d){
		if(SystemUtils.properties == null){
			loadPropertiesWithDefaults();
		}
		return SystemUtils.properties.getProperty(s,d);
	}
	public static String getProperty(String s){
		if(SystemUtils.properties == null){
			loadPropertiesWithDefaults();
		}
		return SystemUtils.properties.getProperty(s);
	}
	
	/**
	 * This function checks to make sure that the configuration
	 * properties that are required for function are set. These shouldn't be
	 * missing as the config.properties.default file should have default values
	 * for all of these items set, so this is just a precaution. Unless you've
	 * been messing with the default config file... How dare you?!
	 * 
	 * @throws RuntimeError Thrown if a required property is missing.
	 */
	public static void checkForRequiredProperties(){ 
		if(SystemUtils.properties == null){
			loadPropertiesWithDefaults();
		}
		for(String s:requiredProperties){
			if (SystemUtils.getProperty(s)==null)
				throw new RuntimeException("Required Property "+s+" is undefined");
		}
	}
}
