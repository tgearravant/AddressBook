package net.tullco.addressbook.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.WordUtils;

public class DisplayUtils {
	public static String capitalize(String s){
		return WordUtils.capitalize(s);
	}
	public static String fullCapitalize(String s){
		return s.toUpperCase();
	}
	public static String dateToString(Date d){
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		return df.format(d);
	}
	public static List<Integer> dateList(){
		ArrayList<Integer> list=new ArrayList<Integer>();
		for (int i=1;i<=31;i++)
			list.add(i);
		return list;
	}
	public static List<Integer> monthList(){
		ArrayList<Integer> list=new ArrayList<Integer>();
		for (int i=1;i<=12;i++)
			list.add(i);
		return list;
	}
	
	public static List<Integer> yearList(){
		ArrayList<Integer> list=new ArrayList<Integer>();
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		for (int i=cal.get(Calendar.YEAR)-100;i<=cal.get(Calendar.YEAR);i++)
			list.add(i);
		return list;
	}
}
