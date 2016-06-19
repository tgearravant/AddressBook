package net.tullco.addressbook.contact;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import org.apache.commons.lang.StringEscapeUtils;

import net.tullco.addressbook.address.Address;
import net.tullco.addressbook.phone_number.PhoneNumber;
//import net.tullco.addressbook.utils.Path;
import net.tullco.addressbook.utils.SQLiteUtils;

public class Contact {
	private int id;
	private String firstName;
	private String lastName;
	private String middleName;
	private String imageLocation;
	private List<Address> addresses;
	private List<PhoneNumber> phoneNumbers;
	
	private static final String INDIVIDUAL_CONTACT_LOADER_SQL="SELECT id,first_name,middle_name,last_name FROM contacts WHERE id=%d";
	private static final String MULTIPLE_CONTACT_LOADER_SQL="SELECT id,first_name,middle_name,last_name FROM contacts WHERE 1=1 %s ORDER BY first_name ASC LIMIT %d OFFSET %d";
	private static final String SAVE_CONTACT_SQL="UPDATE contacts SET first_name=%s, middle_name=%s, last_name=%s WHERE id=%d";

	private Contact (Map<String,String> values){
		setValuesFromMap(values);
		this.addresses = Address.addressesLoader(this.id);
		this.phoneNumbers = PhoneNumber.phoneNumbersLoader(this.id);
	}
	public boolean save(){
		String statement=String.format(SAVE_CONTACT_SQL, this.firstName,this.middleName,this.lastName,this.id);
		return SQLiteUtils.executeUpdate(statement);
	}
	public int getId(){
		return id;
	}
	public boolean hasAddress(){
		return !this.addresses.isEmpty();
	}
	public Address currentAddress(){
		return Address.getCurrentAddress(this.addresses);
	}
	public List<Address> addresses(){
		return this.addresses;
	}
 	public String fullName(){
		return this.firstName+" "+this.lastName;
	}
	public String getImageLocation(){
		if (this.imageLocation==null)
			return "missing_picture.jpg";
		return this.imageLocation;
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
	public static Contact ContactLoader(int id){
		String statement=String.format(INDIVIDUAL_CONTACT_LOADER_SQL,id);
		ResultSet rs = SQLiteUtils.executeSelect(statement);
		try {
			if(!rs.isBeforeFirst())
				return null;
			rs.next();
			return new Contact(convertResultSetToContactMap(rs));
		} catch (SQLException e) {
			return null;
		}
	}
	public static List<Contact> ContactsLoader(String where,int limit,int offset){
		String statement=String.format(MULTIPLE_CONTACT_LOADER_SQL,where,limit,offset);
		//System.out.println(statement);
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
		contact.put("id", Integer.toString(rs.getInt("id")));
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
			if(k.equals("id"))
				this.id=Integer.parseInt(values.get(k));
		}
	}
}
