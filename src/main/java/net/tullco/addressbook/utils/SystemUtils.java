package net.tullco.addressbook.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import net.tullco.addressbook.App;

public class SystemUtils {
	final private static Boolean is_windows=(System.getProperty("os.name").contains("Windows"));
	private static Properties properties=null;
	/**
	 * Takes a Unix type path and, if it detects a Windows operating system, converts it to Windows.
	 * @param path A Unix path
	 * @return Returns a path string appropriate for the OS.
	 */
	public static String adjustPathForOS(String path){
		if (is_windows){
			return path.replace('/', '\\');
		}
		else
			return path;
	}
	public static boolean inProduction(){
		String env=System.getenv("");
		if (env != null)
			return true;
		else
			return false;
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
			}catch(IOException e){}
		}
		return p;
	}
	public static String getProperty(String s){
		if(SystemUtils.properties == null){
			Properties defaultProps = new Properties();
			loadProperties(defaultProps,"config.properties.default");
			Properties p=new Properties(defaultProps);
			loadProperties(p,"config.properties");
			SystemUtils.properties=p;
		}
		return SystemUtils.properties.getProperty(s);
	}
}
