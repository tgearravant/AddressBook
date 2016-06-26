package net.tullco.addressbook.phone_number;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tullco.addressbook.utils.SQLUtils;

public class PhoneNumber {
	private int id=0;
	private int contact_id=0;
	private boolean preferred=false;
	private String type;
	private String number;
	private String locale;

	private static final String[] allowedTypes={"mobile","home","work","other"};
	
	private static final String PHONE_NUMBER_LOADER_SQL="SELECT * FROM phone_numbers WHERE id=%d";
	private static final String PHONE_NUMBERS_LOADER_SQL="SELECT * FROM phone_numbers WHERE contact_id=%d";
	private static final String PHONE_NUMBER_UPDATE_SQL="UPDATE phone_numbers "
			+ "SET type=%s,number=%d,locale=%d";
	private static final String PHONE_NUMBER_INSERT_SQL="INSERT INTO phone_numbers "
			+ "(contact_id,preferred,type,number,locale) "
			+ "VALUES (%d,0,%s,%s,%s)";
	private static final String PHONE_NUMBER_DEACTIVATOR_SQL="UPDATE phone_numbers SET preferred=0 WHERE contact_id=%d AND type=%s";
	private static final String PHONE_NUMBER_ACTIVATOR_SQL="UPDATE phone_numbers SET preferred=1 WHERE id=%d";
	
	public PhoneNumber(Map<String,String> values){
		setValuesFromMap(values);
	}
	public boolean save(){
		if (this.contact_id==0)
			return false;
		if (this.id == 0){
			String statement=SQLUtils.sqlSafeFormat(PHONE_NUMBER_INSERT_SQL,this.contact_id
					,this.type
					,this.number
					,this.locale);
			int newId=SQLUtils.executeInsert(statement);
			this.id=newId;
		}
		else{
			String statement=SQLUtils.sqlSafeFormat(PHONE_NUMBER_UPDATE_SQL
					,this.type
					,this.number
					,this.locale
					,this.id);
			SQLUtils.executeUpdate(statement);
		}
		if (this.preferred){
			String statement=SQLUtils.sqlSafeFormat(PHONE_NUMBER_DEACTIVATOR_SQL,this.contact_id,this.type);
			SQLUtils.executeUpdate(statement);
			statement=SQLUtils.sqlSafeFormat(PHONE_NUMBER_ACTIVATOR_SQL, this.id);
			SQLUtils.executeUpdate(statement);
		}
		return true;
	}

	private static Map<String,String> convertResultSetToPhoneNumberMap(ResultSet rs) throws SQLException{
		String[] fields={"type","number","locale"};
		HashMap<String,String> contact=new HashMap<String, String>();
		for(String s:fields){
			contact.put(s, rs.getString(s));
		}
		contact.put("preferred", Integer.toString(rs.getInt("preferred")));
		//System.out.println(Integer.toString(rs.getInt("preferred")));
		contact.put("id", Integer.toString(rs.getInt("id")));
		contact.put("contact_id", Integer.toString(rs.getInt("contact_id")));
		return contact;
	}

	private void setValuesFromMap(Map<String,String> values){
		for (String k: values.keySet()){
			if(k.equals("contact_id"))
				this.contact_id=Integer.parseInt(values.get(k));
			if(k.equals("type"))
				this.setType(values.get(k));
			if(k.equals("number"))
				this.number = values.get(k);
			if(k.equals("locale"))
				this.locale = values.get(k);
			if(k.equals("id"))
				this.id = Integer.parseInt(values.get(k));
			if(k.equals("preferred"))
				//System.out.println("Contact ID: "+this.contact_id+ "ID: "+this.id+ "Preferred: "+(values.get(k).equals("1") ? true : false)+" or "+values.get(k));
				this.preferred=(values.get(k).equals("1") ? true : false);
		}
	}
	public static List<PhoneNumber> phoneNumbersLoader(int contact_id){
		String statement = String.format(PHONE_NUMBERS_LOADER_SQL, contact_id);
		ResultSet rs = SQLUtils.executeSelect(statement);
		ArrayList<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();
		try {
			while(rs.next()){
				phoneNumbers.add(new PhoneNumber(convertResultSetToPhoneNumberMap(rs)));
			}
			rs.close();
		} catch (SQLException e) {
			System.err.println("Strange error here.");
		}
		return phoneNumbers;
	}
	public static PhoneNumber phoneNumberLoader(int phone_number_id){
		String statement = String.format(PHONE_NUMBER_LOADER_SQL, phone_number_id);
		ResultSet rs = SQLUtils.executeSelect(statement);
		try {
			if(!rs.isBeforeFirst())
				return null;
			rs.next();
			PhoneNumber pn = new PhoneNumber(convertResultSetToPhoneNumberMap(rs));
			rs.close();
			return pn;
		} catch (SQLException e) {
			return null;
		}
	}
	public int getId() {
		return id;
	}
	public int getContactId() {
		return contact_id;
	}
	public boolean isPreferred() {
		return preferred;
	}
	public void setPreferred(boolean preferred) {
		this.preferred = preferred;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getLocale() {
		return locale;
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}
	public static PhoneNumber getPreferredNumberOfType(List<PhoneNumber> phoneNumbers,String type){
		for(PhoneNumber p: phoneNumbers){
			if (p.getType().equals(type) && p.isPreferred())
				return p;
		}
		return null;
	}
	public static List<PhoneNumber> getPreferredNumbers(List<PhoneNumber> phoneNumbers){
		ArrayList<PhoneNumber> typeNumbers = new ArrayList<PhoneNumber>();
		for(PhoneNumber p: phoneNumbers){
			if (p.isPreferred())
				typeNumbers.add(p);
		}
		return typeNumbers;
	}
	public static List<PhoneNumber> getNumbersOfType(List<PhoneNumber> phoneNumbers,String type){
		ArrayList<PhoneNumber> typeNumbers = new ArrayList<PhoneNumber>();
		for(PhoneNumber p: phoneNumbers){
			if (p.getType().equals(type))
				typeNumbers.add(p);
		}
		return typeNumbers;
	}
	public static String[] getAllowedTypes(){
		return allowedTypes;
	}
}