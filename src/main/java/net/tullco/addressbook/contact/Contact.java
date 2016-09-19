package net.tullco.addressbook.contact;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import org.apache.commons.lang.StringEscapeUtils;

import org.json.JSONObject;

import net.tullco.addressbook.address.Address;
import net.tullco.addressbook.phone_number.PhoneNumber;
import net.tullco.addressbook.utils.DisplayUtils;
import net.tullco.addressbook.utils.Path;
//import net.tullco.addressbook.utils.Path;
import net.tullco.addressbook.utils.SQLUtils;
import net.tullco.addressbook.vcard.VCard;

public class Contact {
	private int id=0;
	private String firstName;
	private String middleName;
	private String lastName;
	private Date birthdate;
	private String email;
	private String nickname;
	private String imageLocation;
	private List<Address> addresses;
	private List<PhoneNumber> phoneNumbers;
	
	private static final String INDIVIDUAL_CONTACT_LOADER_SQL="SELECT * FROM contacts WHERE id=%d";
	private static final String MULTIPLE_CONTACT_LOADER_SQL="SELECT * FROM contacts WHERE 1=1 %s ORDER BY last_name,first_name ASC LIMIT %d OFFSET %d";
	private static final String SAVE_CONTACT_SQL="UPDATE contacts "
			+ "SET first_name=%s,middle_name=%s,last_name=%s,birthdate=%d,email=%s,nickname=%s "
			+ "WHERE id=%d";
	private static final String CONTACT_INSERT_SQL="INSERT INTO contacts "
			+ "(first_name,middle_name,last_name,birthdate,email,nickname) "
			+ "VALUES (%s,%s,%s,%d,%s,%s)";
	private static final String CONTACT_DELETION_SQL="DELETE FROM contacts WHERE id=%d";
	private static final String RELATED_CONTACT_SQL="SELECT c.* FROM contact_addresses ca INNER JOIN contacts c ON c.id=ca.contact_id WHERE address_id IN (SELECT address_id FROM contact_addresses WHERE contact_id=%d) AND contact_id <> %d";

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
					,(this.birthdate==null?
							null:(this.birthdate.getTime()))
					,this.email
					,this.nickname);
			this.id=SQLUtils.executeInsert(statement);
			return (this.id==0?false:true);
		}
		String statement=SQLUtils.sqlSafeFormat(SAVE_CONTACT_SQL
				,this.firstName
				,this.middleName
				,this.lastName
				,(this.birthdate==null?
						null:(this.birthdate.getTime()))
				,this.email
				,this.nickname
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
		return (this.firstName==null?"":this.firstName+" ")+(this.nickname()==null?"":"\""+this.nickname()+"\" ")+(this.lastName==null?"":this.lastName);
	}
	public String getImageLocation(){
		if (this.imageLocation==null)
			return "missing_picture.jpg";
		return this.imageLocation;
	}
	public String firstName(){
		return this.firstName;
	}
	public void setFirstName(String firstname){
		this.firstName=firstname;
	}
	public String lastName(){
		return this.lastName;
	}
	public void setLastName(String lastname){
		this.lastName=lastname;
	}
	public String middleName(){
		return this.middleName;
	}
	public void setMiddleName(String middlename){
		this.middleName=middlename;
	}
	public Date birthdate(){
		return this.birthdate;
	}
	public void setBirthdate(Date birthdate){
		this.birthdate=birthdate;
	}
	public String birthdate_formatted(){
		return DisplayUtils.dateToString(this.birthdate);
	}
	public String email(){
		return this.email;
	}
	public void setEmail(String email){
		this.email=email;
	}
	public String nickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getVcard(){
		if(this.firstName!=null && this.lastName!=null)
			return new VCard(this).getVCardAsString();
		return null;
	}
	public String getVCardPath(){
		if(this.firstName!=null && this.lastName!=null){
			String encodedName;
			try {
				encodedName = URLEncoder.encode(this.firstName(), "US-ASCII")+"_"+URLEncoder.encode(this.lastName(), "US-ASCII");
			} catch (UnsupportedEncodingException e) {
				return null;
			}
			return Path.Web.GET_VCARD.replace(":contact_id", Integer.toString(this.getId())).replace(":name", encodedName);
		}
		return null;
	}
	public List<Contact> getRelatedContacts(){
		String statement = SQLUtils.sqlSafeFormat(RELATED_CONTACT_SQL,this.id,this.id);
		ResultSet rs = SQLUtils.executeSelect(statement);
		ArrayList<Contact> contacts = new ArrayList<Contact>();
		try {
			while(rs.next())
				contacts.add(new Contact(convertResultSetToContactMap(rs)));
			} catch (SQLException e) {}
		return contacts;
	}
	public static Contact contactLoader(int id){
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
		String[] fields={"first_name","middle_name","last_name","email","nickname"};
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
			if(k.equals("nickname"))
				this.nickname=values.get(k);
			if(k.equals("birthdate")){
				if(values.get(k).equals("0"))
					this.birthdate=null;
				else
					this.birthdate=new Date(Long.parseLong(values.get(k)));
			}
		}
		if (this.birthdate==null){
			if(values.get("birthyear")!=null&&values.get("birthmonth")!=null&&values.get("birthday")!=null){
				Calendar cal = Calendar.getInstance();
				cal.clear();
				cal.set(Integer.parseInt(values.get("birthyear"))
						,Integer.parseInt(values.get("birthmonth"))-1
						,Integer.parseInt(values.get("birthday")));
				this.birthdate=cal.getTime();
			}
		}
	}
	@Override
	public boolean equals(Object c){
		if(c==null)
			return false;
		if(this==c)
			return true;
		if(c instanceof Contact)
			return ((Contact) c).id==this.id;
		return false;
	}
	public JSONObject toJSON(){
		JSONObject json = new JSONObject();
		json.put("id", this.id);
		json.put("first_name", this.firstName);
		json.put("middle_name", this.middleName);
		json.put("last_name", this.lastName);
		json.put("birthdate", DisplayUtils.dateToString(this.birthdate));
		json.put("nickname", this.nickname);
		json.put("email", this.email);
		for(Address a : this.addresses){
			json.append("addresses", a.toJson());
		}
		for(PhoneNumber pn: this.phoneNumbers){
			json.append("phone_numbers", pn.toJson());
		}
		return json;
	}
}
