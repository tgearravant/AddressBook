package net.tullco.addressbook.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocaleUtils {
	private static final String GET_LOCALES_SQL="SELECT locale,long_name "
			+ "FROM locales "
			+ "ORDER BY CASE locale WHEN 'us' THEN 'AAAA' WHEN 'ca' THEN 'AAAB' ELSE long_name END ASC";
	private static final String ADD_LOCALE_SQL="INSERT INTO locales (locale,long_name) VALUES (%s,%s)";
	private static Map<String,String> cachedLocales=null;
	private static List<String> cachedOrderedLocales=null;
	
	/**
	 * This fetches the locales from the database, and returns them as a map, with the keys being two letter country codes,
	 * and the values being full country names. This list is cached to prevent frequent calls to the DB. If you do something
	 * that you expect will change this, please also use the expireLocaleCache() function to assure that it is reloaded.
	 * @return A map of allowed locales.
	 */
	public static Map<String,String> allowedLocales(){
		if(cachedLocales!=null)
			return cachedLocales;
		ResultSet rs = SQLUtils.executeSelect(GET_LOCALES_SQL);
		HashMap<String,String> locales = new HashMap<String,String>();
		ArrayList<String> orderedLocales = new ArrayList<String>();
		
		try {
			while(rs.next()){
				orderedLocales.add(rs.getString("locale"));
				locales.put(rs.getString("locale"), rs.getString("long_name"));
			}
			rs.close();
		} catch (SQLException e) {}
		cachedLocales=locales;
		cachedOrderedLocales = orderedLocales;
		return cachedLocales;
	}
	
	/**
	 * This returns the available locales as a list of arrays. For each array, the value in 0 is the two letter code,
	 * and the value in 1 is the full country name.
	 * @return A list of locale arrays of length 2.
	 */
	public static List<String[]> allowedLocalesList(){
		Map<String,String> locales=allowedLocales();
		ArrayList<String[]> localesArray = new ArrayList<String[]>();
		
		for(String locale: cachedOrderedLocales){
			String[] l = new String[2];
			l[0]=locale;
			l[1]=locales.get(locale);
			localesArray.add(l);
		}
		return localesArray;
	}
	
	/**
	 * This gets the long locale name for a given two letter code.
	 * @param locale The two letter code of the country of interest.
	 * @return The full country name.
	 */
	public static String getLongLocaleName(String locale){
		String l = allowedLocales().get(locale);
		if (l==null){
			l="Invalid Locale";
		}
		return l;
	}
	
	/**
	 * This function expires the locale cache, causing it to be reloaded the next time it is called.
	 */
	public static void expireLocaleCache(){
		cachedLocales=null;
		cachedOrderedLocales=null;
	}
	
	/**
	 * This adds a locale to the database, and expires the cache so that the new list will be reloaded
	 * @param locale The two letter code for the locale.
	 * @param long_name The full name corresponding to the locale.
	 */
	public static void addLocale(String locale, String long_name){
		String statement = SQLUtils.sqlSafeFormat(ADD_LOCALE_SQL, locale, long_name);
		SQLUtils.executeInsert(statement);
		expireLocaleCache();
	}
}
