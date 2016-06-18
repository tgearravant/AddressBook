package net.tullco.addressbook.contact;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import org.apache.commons.lang.StringEscapeUtils;

import net.tullco.addressbook.utils.SQLiteUtils;

public class Contact {
	private String firstName;
	private String lastName;
	private String middleName;
	private String street;
	private String zipCode;
	private String city;
	private String state;
	private String country;
	
	private static final String INDIVIDUAL_CONTACT_LOADER_SQL="SELECT first_name,middle_name,last_name FROM contacts WHERE id=%d";
	private static final String MULTIPLE_CONTACT_LOADER_SQL="SELECT first_name,middle_name,last_name FROM contacts WHERE 1=1 %s ORDER BY first_name ASC LIMIT %d OFFSET %d";

	private Contact (Map<String,String> values){
		setValuesFromMap(values);
	}
	public String fullName(){
		return this.firstName+" "+this.lastName;
	}
	public String firstName(){
		return this.firstName;
	}
	public String lastName(){
		return this.lastName;
	}
	public String middleName(){
		return this.middleName;
	}
	public String street(){
		return this.street;
	}
	public String zipCode(){
		return this.zipCode;
	}
	public String city(){
		return this.city;
	}
	public String state(){
		return this.state;
	}
	public String country(){
		return this.country;
	}
	public static Contact ContactLoader(int id){
		Map<String,String> contact = new HashMap<String,String>();
		if(id==0){
			contact.put("first_name","Tull");
			contact.put("last_name","Gearreald");
			contact.put("middle_name", "Neal");
			contact.put("street", "901 S. Ashland Ave.");
			contact.put("zip_code", "60607");
			contact.put("city", "Chicago");
			contact.put("state", "IL");
			contact.put("country", "US");
			return new Contact(contact);
		}
		
		String statement=String.format(INDIVIDUAL_CONTACT_LOADER_SQL,id);
		ResultSet rs = SQLiteUtils.executeSelect(statement);
		try {
			if(!rs.isBeforeFirst())
				return null;
			
			return new Contact(convertResultSetToContactMap(rs));
		} catch (SQLException e) {
			return null;
		}
	}
	public static List<Contact> ContactsLoader(String where,int limit,int offset){
		String statement=String.format(MULTIPLE_CONTACT_LOADER_SQL,where,limit,offset);
		System.out.println(statement);
		ResultSet rs = SQLiteUtils.executeSelect(statement);
		ArrayList<Contact> contacts = new ArrayList<Contact>();
		try {
			while(rs.next()){
				contacts.add(new Contact(convertResultSetToContactMap(rs)));
			}
		} catch (SQLException e) {
			System.err.println("Strange error here.");
		}
		return contacts;
	}
	private static Map<String,String> convertResultSetToContactMap(ResultSet rs) throws SQLException{
		String[] fields={"first_name","middle_name","last_name"};
		HashMap<String,String> contact=new HashMap<String, String>();
		for(String s:fields){
			contact.put(s, rs.getString(s));
		}
		return contact;
	}
	private void setValuesFromMap(Map<String,String> values){
		for (String k: values.keySet()){
			if(k.equals("first_name"))
				this.firstName = values.get(k);
			if(k.equals("last_name"))
				this.lastName = values.get(k);
			if(k.equals("middle_name"))
				this.middleName = values.get(k);
			if(k.equals("street"))
				this.street = values.get(k);
			if(k.equals("zip_code"))
				this.zipCode = values.get(k);
			if(k.equals("city"))
				this.city = values.get(k);
			if(k.equals("state"))
				this.state = values.get(k);
			if(k.equals("country"))
				this.country = values.get(k);
		}
	}
}
