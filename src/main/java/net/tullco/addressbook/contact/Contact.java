package net.tullco.addressbook.contact;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import org.apache.commons.lang.StringEscapeUtils;


import net.tullco.addressbook.address.Address;
import net.tullco.addressbook.phone_number.PhoneNumber;
import net.tullco.addressbook.utils.DisplayUtils;
//import net.tullco.addressbook.utils.Path;
import net.tullco.addressbook.utils.SQLUtils;

public class Contact {
	private int id=0;
	private String firstName;
	private String middleName;
	private String lastName;
	private Date birthdate;
	private String email;
	private String imageLocation;
	private List<Address> addresses;
	private List<PhoneNumber> phoneNumbers;
	
	private static final String INDIVIDUAL_CONTACT_LOADER_SQL="SELECT * FROM contacts WHERE id=%d";
	private static final String MULTIPLE_CONTACT_LOADER_SQL="SELECT * FROM contacts WHERE 1=1 %s ORDER BY last_name,first_name ASC LIMIT %d OFFSET %d";
	private static final String SAVE_CONTACT_SQL="UPDATE contacts "
			+ "SET first_name=%s,middle_name=%s,last_name=%s,birthdate=%d,email=%s "
			+ "WHERE id=%d";
	private static final String CONTACT_INSERT_SQL="INSERT INTO contacts "
			+ "(first_name,middle_name,last_name,birthdate,email) "
			+ "VALUES (%s,%s,%s,%d,%s)";
	private static final String CONTACT_DELETION_SQL="DELETE FROM contacts WHERE id=%d";

	public Contact (Map<String,String> values){
		setValuesFromMap(values);
		this.addresses = Address.addressesLoader(this.id);
		this.phoneNumbers = PhoneNumber.phoneNumbersLoader(this.id);
	}
	public boolean save(){
		if (this.id==0){
			String statement=SQLUtils.sqlSafeFormat(CONTACT_INSERT_SQL 
					,this.firstName
					,this.middleName
					,this.lastName
					,this.birthdate.getTime()
					,this.email);
			this.id=SQLUtils.executeInsert(statement);
			return (this.id==0?false:true);
		}
		String statement=SQLUtils.sqlSafeFormat(SAVE_CONTACT_SQL
				,this.firstName
				,this.middleName
				,this.lastName
				,this.birthdate.getTime()
				,this.email
				,this.id);
		return SQLUtils.executeUpdate(statement);
	}
	public boolean delete(){
		if (this.id==0)
			return false;
		for(Address a:this.addresses){
			a.delete();
		}for(PhoneNumber pn:this.phoneNumbers){
			pn.delete();
		}
		String statement = SQLUtils.sqlSafeFormat(CONTACT_DELETION_SQL, this.id);
		return SQLUtils.executeUpdate(statement);
	}
	public int getId(){
		return id;
	}
	public boolean hasAddress(){
		return (this.currentAddress()!=null);
	}
	public Address currentAddress(){
		return Address.getCurrentAddress(this.addresses);
	}
	public boolean hasPhoneNumberOfType(String type){
		return !PhoneNumber.getNumbersOfType(this.phoneNumbers,type).isEmpty();
	}
	public boolean hasPreferredPhoneNumbers(){
		return !PhoneNumber.getPreferredNumbers(this.phoneNumbers).isEmpty();
	}
	public boolean hasPreferredPhoneNumberOfType(String type){
		return !(PhoneNumber.getPreferredNumberOfType(this.phoneNumbers,type)==null);
	}
	public PhoneNumber getPreferredPhoneNumberOfType(String type){
		return PhoneNumber.getPreferredNumberOfType(this.phoneNumbers, type);
	}
	public List<PhoneNumber> getPreferredPhoneNumbers(){
		return PhoneNumber.getPreferredNumbers(this.phoneNumbers);
	}
	public List<PhoneNumber> getPhoneNumbersOfType(String type){
		return PhoneNumber.getNumbersOfType(this.phoneNumbers, type);
	}
	public List<Address> addresses(){
		return this.addresses;
	}
	public List<PhoneNumber> phoneNumbers(){
		return this.phoneNumbers;
	}
 	public String fullName(){
		return (this.firstName==null?"":this.firstName+" ")+(this.lastName==null?"":this.lastName);
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
	public Date birthdate(){
		return this.birthdate;
	}
	public String birthdate_formatted(){
		return DisplayUtils.dateToString(this.birthdate);
	}
	public String email(){
		return this.email;
	}
	public static Contact ContactLoader(int id){
		String statement=String.format(INDIVIDUAL_CONTACT_LOADER_SQL,id);
		ResultSet rs = SQLUtils.executeSelect(statement);
		try {
			if(!rs.isBeforeFirst())
				return null;
			rs.next();
			Contact c=new Contact(convertResultSetToContactMap(rs));
			rs.close();
			return c;
		} catch (SQLException e) {
			return null;
		}
	}
	
	/**
	 * This loads all contacts in the database alphabetically by last name, then by first name.
	 * Limit and offset define which contacts to load.
	 * Please note that the where option is /not/ escaped.
	 * Please make sure that your where statement is already safe before adding it here.
	 * @param where A SQL where clause that is used to limit the contacts loaded
	 * @param limit The number of contacts to load
	 * @param offset The number of contacts to forward. Defaults to 0.
	 * @return A list of the contacts that were loaded.
	 */
	public static List<Contact> ContactsLoader(String where,int limit,int offset){
		String statement=String.format(MULTIPLE_CONTACT_LOADER_SQL,where,limit,offset);
		//System.out.println(statement);
		ResultSet rs = SQLUtils.executeSelect(statement);
		ArrayList<Contact> contacts = new ArrayList<Contact>();
		try {
			while(rs.next()){
				contacts.add(new Contact(convertResultSetToContactMap(rs)));
			}
			rs.close();
		} catch (SQLException e) {
			System.err.println("Strange error here.");
		}
		return contacts;
	}
	private static Map<String,String> convertResultSetToContactMap(ResultSet rs) throws SQLException{
		String[] fields={"first_name","middle_name","last_name","email"};
		HashMap<String,String> contact=new HashMap<String, String>();
		for(String s:fields){
			contact.put(s, rs.getString(s));
		}
		long epoch=rs.getLong("birthdate");
		contact.put("birthdate", Long.toString(epoch));
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
			if(k.equals("email"))
				this.email=values.get(k);
			if(k.equals("birthdate")){
				this.birthdate=new Date(Long.parseLong(values.get(k)));
			}
		}
		if (this.birthdate==null){
			if(values.containsKey("birthyear")&&values.containsKey("birthmonth")&&values.containsKey("birthday")){
				Calendar cal = Calendar.getInstance();
				cal.set(Integer.parseInt(values.get("birthyear"))
						,Integer.parseInt(values.get("birthmonth"))-1
						,Integer.parseInt(values.get("birthday")));
				this.birthdate=cal.getTime();
				
			}
		}
	}
}
