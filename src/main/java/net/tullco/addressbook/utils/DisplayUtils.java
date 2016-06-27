package net.tullco.addressbook.utils;

import org.apache.commons.lang.WordUtils;

public class DisplayUtils {
	public static String capitalize(String s){
		return WordUtils.capitalize(s);
	}
	public static String fullCapitalize(String s){
		return s.toUpperCase();
	}
}
