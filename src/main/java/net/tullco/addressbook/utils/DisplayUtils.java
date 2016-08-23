package net.tullco.addressbook.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.WordUtils;

public class DisplayUtils {
	
	/**
	 * Capitalizes all whitespace separated words in a string.
	 * @param s The string to capitalize.
	 * @return The capitalized string.
	 */
	public static String capitalize(String s){
		return WordUtils.capitalize(s);
	}
	
	/**
	 * Converts a string to all uppercase.
	 * @param s The string to convert.
	 * @return The string in uppercase.
	 */
	public static String fullCapitalize(String s){
		return s.toUpperCase();
	}
	
	/**
	 * Converts a date to a mm/dd/yyyy string.
	 * @param d The date object to convert.
	 * @return A string representing the date.
	 */
	public static String dateToString(Date d){
		if(d==null)
			return null;
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		return df.format(d);
	}
	
	/**
	 * Get a list of all the dates for the birthday dropdowns.
	 * @return A list of numbers from 1 to 31.
	 */
	public static List<Integer> dateList(){
		ArrayList<Integer> list=new ArrayList<Integer>();
		for (int i=1;i<=31;i++)
			list.add(i);
		return list;
	}
	
	/**
	 * Get a list of all the months for the birthday dropdowns
	 * @return A list of numbers from 1 to 12.
	 */
	public static List<Integer> monthList(){
		ArrayList<Integer> list=new ArrayList<Integer>();
		for (int i=1;i<=12;i++)
			list.add(i);
		return list;
	}
	
	/**
	 * Get a list of years for the birthday dropdowns.
	 * @return A list of years from 100 years ago to today.
	 */
	public static List<Integer> yearList(){
		ArrayList<Integer> list=new ArrayList<Integer>();
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		for (int i=cal.get(Calendar.YEAR)-100;i<=cal.get(Calendar.YEAR);i++)
			list.add(i);
		return list;
	}
}
