package net.tullco.addressbook.utils;

public class SystemUtils {
	final private static Boolean is_windows=(System.getProperty("os.name").contains("Windows"));
	
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
}
