package net.tullco.addressbook.address;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tullco.addressbook.contact.Contact;
import net.tullco.addressbook.utils.LocaleUtils;
import net.tullco.addressbook.utils.Path;
import net.tullco.addressbook.utils.SQLUtils;

public class Address {
	private int id=0;
	private int contact_id=0;
	private String street;
	private String apartment;
	private String zipCode;
	private String city;
	private String state;
	private String country;
	private boolean active=false;

	private static final String ADDRESSES_LOADER_SQL="SELECT a.*,ca.current_address,ca.contact_id "
			+ "FROM contact_addresses AS ca "
			+ "INNER JOIN addresses AS a ON a.id=ca.address_id "
			+ "WHERE ca.contact_id=%d";
	private static final String ADDRESS_LOADER_SQL="SELECT *,ca.current_address,ca.contact_id "
			+ "FROM addresses AS a "
			+ "INNER JOIN contact_addresses ca ON ca.address_id=a.id "
			+ "WHERE a.id=%d AND ca.contact_id=%d";
	private static final String ADDRESS_CONTACT_FINDER="SELECT EXISTS(SELECT 1 FROM contact_addresses WHERE address_id=%d AND contact_id=%d) AS has_record";
	private static final String ADDRESS_CONTACT_INSERT="INSERT INTO contact_addresses (address_id,contact_id,current_address) VALUES (%d,%d,0)";
	private static final String ADDRESS_DEACTIVATOR_SQL="UPDATE contact_addresses SET current_address=0 WHERE contact_id = %d";
	private static final String ADDRESS_SINGLE_DEACTIVATOR_SQL="UPDATE contact_addresses SET current_address=0 WHERE address_id = %d AND contact_id = %d";
	private static final String ADDRESS_ACTIVATOR_SQL="UPDATE contact_addresses SET current_address=1 WHERE address_id = %d AND contact_id=%d";
	private static final String ADDRESS_UPDATE_SQL="UPDATE addresses "
			+ "SET street=%s,apartment=%s,zip_code=%s,city=%s,state=%s,country=%s "
			+ "WHERE id=%d";
	private static final String ADDRESS_INSERT_SQL="INSERT INTO addresses "
			+ "(street,apartment,zip_code,city,state,country) "
			+ "VALUES (%s,%s,%s,%s,%s,%s)";
	private static final String ADDRESS_DELETE_SQL="DELETE FROM contact_addresses WHERE address_id=%d AND contact_id=%d";
	private static final String ADDRESS_NAME_SEARCH_SQL="SELECT a.*,ca.contact_id,ca.current_address "
			+ "FROM addresses a "
			+ "INNER JOIN contact_addresses ca ON ca.address_id=a.id "
			+ "INNER JOIN contacts c ON c.id=ca.contact_id "
			+ "WHERE (lower(first_name) LIKE lower(%s) OR lower(last_name) LIKE lower(%s)) "
			+ "ORDER BY c.last_name,c.first_name "
			+ "LIMIT 30";
	private static final String ORPHAN_ADDRESS_FINDER_SQL="SELECT count(*) AS count_addresses FROM contact_addresses WHERE address_id=%d";
	private static final String ORPHAN_ADDRESS_DELETER_SQL="DELETE FROM addresses WHERE id=%d";

	public Address(Map<String,String> values){
		setValuesFromMap(values);
	}
	private void setValuesFromMap(Map<String,String> values){
		for (String k: values.keySet()){
			if(values.get(k)==null || values.get(k).equals(""))
				continue;
			if(k.equals("street"))
				this.street = values.get(k);
			if(k.equals("contact_id"))
				this.contact_id = Integer.parseInt(values.get(k));
			if(k.equals("apartment"))
				this.apartment = values.get(k);
			if(k.equals("zip_code"))
				this.zipCode = values.get(k);
			if(k.equals("city"))
				this.city = values.get(k);
			if(k.equals("state"))
				this.state = values.get(k);
			if(k.equals("country"))
				this.country = values.get(k);
			if(k.equals("id"))
				this.id=Integer.parseInt(values.get(k));
			if(k.equals("active"))
				this.active=(values.get(k).equals("1") ? true : false);
		}
	}
	public boolean save(){
		if (this.contact_id==0)
			return false;
		if (this.id == 0){
			String statement=SQLUtils.sqlSafeFormat(ADDRESS_INSERT_SQL
					,this.street
					,this.apartment
					,this.zipCode
					,this.city
					,this.state
					,this.country);
			int newId=SQLUtils.executeInsert(statement);
			this.id=newId;
		}
		else{
			String statement=SQLUtils.sqlSafeFormat(ADDRESS_UPDATE_SQL
					,this.street
					,this.apartment
					,this.zipCode
					,this.city
					,this.state
					,this.country
					,this.id);
			SQLUtils.executeUpdate(statement);
		}
		if (!this.hasContactLinkage()){
			String statement=SQLUtils.sqlSafeFormat(ADDRESS_CONTACT_INSERT, this.id,this.contact_id);
			SQLUtils.executeInsert(statement);
		}
		if (this.active){
			String statement=SQLUtils.sqlSafeFormat(ADDRESS_DEACTIVATOR_SQL,this.contact_id);
			SQLUtils.executeUpdate(statement);
			statement=SQLUtils.sqlSafeFormat(ADDRESS_ACTIVATOR_SQL, this.id,this.contact_id);
			SQLUtils.executeUpdate(statement);
		}else{
			String statement=SQLUtils.sqlSafeFormat(ADDRESS_SINGLE_DEACTIVATOR_SQL,this.id,this.contact_id);
			SQLUtils.executeUpdate(statement);
		}
		return true;
	}
	public void delete(){
		String statement = SQLUtils.sqlSafeFormat(ADDRESS_DELETE_SQL,this.id,this.contact_id);
		SQLUtils.executeUpdate(statement);
		statement = SQLUtils.sqlSafeFormat(ORPHAN_ADDRESS_FINDER_SQL, this.id);
		ResultSet rs = SQLUtils.executeSelect(statement);
		try{
			rs.next();
			int i = rs.getInt("count_addresses");
			rs.close();	
			if (i==0){
				statement = SQLUtils.sqlSafeFormat(ORPHAN_ADDRESS_DELETER_SQL, this.id);
				SQLUtils.executeUpdate(statement);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		
	}
	public boolean hasContactLinkage(){
		String statement = SQLUtils.sqlSafeFormat(ADDRESS_CONTACT_FINDER, this.id,this.contact_id);
		ResultSet rs = SQLUtils.executeSelect(statement);
		try{
			rs.next();
			int i = rs.getInt("has_record");
			rs.close();
			if(i==1)
				return true;
			else
				return false;
		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
		
	}
	public Contact getContact(){
		return Contact.contactLoader(this.contact_id);
	}
	public int id(){
		return this.id;
	}
	public int contactId(){
		return this.contact_id;
	}
	public String street(){
		return this.street;
	}
	public String apartment(){
		return this.apartment;
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
	public String getLocale(){
		return this.country;
	}
	public String country(){
		return LocaleUtils.getLongLocaleName(this.country);
	}
	public boolean active(){
		return this.active;
	}
	public String getEditURL(){
		return Path.Web.getAddressEdit(this.contact_id, this.id);
	}
	public void setContactId(int contact_id){
		this.contact_id=contact_id;
	}
	private static Map<String,String> convertResultSetToAddressMap(ResultSet rs) throws SQLException{
		String[] fields={"street","apartment","zip_code","city","state","country"};
		HashMap<String,String> contact=new HashMap<String, String>();
		for(String s:fields){
			contact.put(s, rs.getString(s));
		}
		contact.put("active", Integer.toString(rs.getInt("current_address")));
		contact.put("id", Integer.toString(rs.getInt("id")));
		contact.put("contact_id", Integer.toString(rs.getInt("contact_id")));
		return contact;
	}

	public static Address addressLoader(int address_id,int contact_id){
		String statement = SQLUtils.sqlSafeFormat(ADDRESS_LOADER_SQL, address_id, contact_id);
		ResultSet rs = SQLUtils.executeSelect(statement);
		try {
			if(!rs.isBeforeFirst())
				return null;
			rs.next();
			Address a = new Address(convertResultSetToAddressMap(rs));
			rs.close();
			return a;
		} catch (SQLException e) {
			return null;
		}
	}
	public static List<Address> addressesLoader(int contact_id){
		String statement = SQLUtils.sqlSafeFormat(ADDRESSES_LOADER_SQL, contact_id);
		ResultSet rs = SQLUtils.executeSelect(statement);
		ArrayList<Address> addresses = new ArrayList<Address>();
		try {
			while(rs.next()){
				addresses.add(new Address(convertResultSetToAddressMap(rs)));
			}
			rs.close();
		} catch (SQLException e) {
			System.err.println("Strange error here.");
		}
		return addresses;
	}
	public static List<Address> addressesLoaderByName(String name){
		ArrayList<Address> addresses = new ArrayList<Address>();
		String search = "%"+name+"%";
		String statement = SQLUtils.sqlSafeFormat(ADDRESS_NAME_SEARCH_SQL,search,search);
		ResultSet rs = SQLUtils.executeSelect(statement);
		try {
			while(rs.next()){
				addresses.add(new Address(convertResultSetToAddressMap(rs)));
			}
			rs.close();
		} catch (SQLException e) {
			System.err.println("Strange error here.");
		}
		return addresses;
	}
	public static Address getCurrentAddress(List<Address> addresses){
		for (Address a: addresses){
			if (a.active())
				return a;
		}
		return null;
	}
	public static Address addAddress(Map<String,String> valueMap){
		Address newAddress=new Address(valueMap);
		newAddress.save();
		return newAddress;
	}
	public static void addAddressLink(int contact_id,int address_id){
		
	}
}