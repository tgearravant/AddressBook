package net.tullco.addressbook.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocaleUtils {
	public static Map<String,String> allowedLocales(){
		HashMap<String,String> locales = new HashMap<String,String>();
		locales.put("us", "United States");
		locales.put("ca", "Canada");
		return locales;
	}
	public static List<String[]> allowedLocalesList(){
		Map<String,String> locales=allowedLocales();
		ArrayList<String[]> localesArray = new ArrayList<String[]>();
		for(String locale:locales.keySet()){
			String[] l = new String[2];
			l[0]=locale;
			l[1]=locales.get(locale);
			localesArray.add(l);
		}
		return localesArray;
	}
	public static String getLongLocaleName(String locale){
		String l = allowedLocales().get(locale);
		if (l==null){
			l="Invalid Locale";
		}
		return l;
	}
}
