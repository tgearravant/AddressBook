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
			+ "WHERE (lower(first_name) LIKE lower(%s) OR lower(last_name) LIKE lower(%s) OR lower(nickname) LIKE lower(%s)) AND c.id <> %d "
			+ "ORDER BY c.last_name,c.first_name "
			+ "LIMIT 30";
	private static final String ORPHAN_ADDRESS_FINDER_SQL="SELECT count(*) AS count_addresses FROM contact_addresses WHERE address_id=%d";
	private static final String ORPHAN_ADDRESS_DELETER_SQL="DELETE FROM addresses WHERE id=%d";

	/**
	 * Constructs a new address from the values in the supplied map. If saved, this address will be saved as a new record in the DB unless the id key is set.
	 * @param values A map of key value pairs. Valid keys are street, contact_id, apartment, zip_code, city, state, country, id, and active. 
	 */
	public Address(Map<String,String> values){
		setValuesFromMap(values);
	}
	/**
	 * Sets the values of the address from a supplied map. This method is used by both the constructor and the loader, so it's pretty important.
	 * @param values The map of values.
	 */
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
	/**
	 * Saves the address to the DB.
	 * 
	 * If the address does not already exist, it will be created. If it does exist, it will be updated. If this address is active, it will, upon saving,
	 * inactivate all other addresses for the contact in question.
	 * @return True. May return false if the save fails, but not currently implemented.
	 */
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
	/**
	 * Unlinks the address from the contact. For realz. Cannot be easily undone.
	 * 
	 * Will also delete the actual address record if this is the last remaining linkage that is getting deleted.
	 */
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
	/**
	 * Checks to see if an address is linked to its contact.
	 * @return True if the linkage is in place. False if it is not.
	 */
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
	//Get/Set Methods... Not documenting each of these today. maybe later if I'm feeling more motivated...
	public int id(){
		return this.id;
	}
	public int contactId(){
		return this.contact_id;
	}
	public String street(){
		return this.street;
	}
	public void setStreet(String street){
		this.street=street;
	}
	public String apartment(){
		return this.apartment;
	}
	public void setApartment(String apartment){
		this.apartment=apartment;
	}
	public String zipCode(){
		return this.zipCode;
	}
	public void setZipCode(String zip){
		this.zipCode=zip;
	}
	public String city(){
		return this.city;
	}
	public void setCity(String city){
		this.city=city;
	}
	public String state(){
		return this.state;
	}
	public void setState(String state){
		this.state=state;
	}
	public String getLocale(){
		return this.country;
	}
	public String country(){
		return LocaleUtils.getLongLocaleName(this.country);
	}
	public void setLocale(String locale){
		this.country=locale;
	}
	public boolean active(){
		return this.active;
	}
	public void setActive(boolean active){
		this.active=active;
	}
	public String getEditURL(){
		return Path.Web.getAddressEdit(this.contact_id, this.id);
	}
	public void setContactId(int contact_id){
		this.contact_id=contact_id;
	}
	public Contact getContact(){
		return Contact.contactLoader(this.contact_id);
	}
	/**
	 * Creates an address map from the results of an address query.
	 * @param rs The results set containing one row with the fields: 
	 * @return The map for use in the address constructor.
	 * @throws SQLException If the results set has horrible problems.
	 */
	private static Map<String,String> convertResultSetToAddressMap(ResultSet rs) throws SQLException{
		String[] fields={"street","apartment","zip_code","city","state","country"};
		HashMap<String,String> addressMap=new HashMap<String, String>();
		for(String s:fields){
			addressMap.put(s, rs.getString(s));
		}
		addressMap.put("active", Integer.toString(rs.getInt("current_address")));
		addressMap.put("id", Integer.toString(rs.getInt("id")));
		addressMap.put("contact_id", Integer.toString(rs.getInt("contact_id")));
		return addressMap;
	}
	/**
	 * Loads an address from the database.
	 * @param address_id The address id of the address.
	 * @param contact_id The contact to whom the address belongs.
	 * @return The address object.
	 */
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
	/**
	 * Loads all the addresses for a given contact into a list.
	 * @param contact_id The contact id for the addresses.
	 * @return The list of all addresses for the contact.
	 */
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
	/**
	 * Loads all the addresses whose name matches the string.
	 * @param name The name search string. case insensitive.
	 * @return A list of all matching addresses.
	 */
	public static List<Address> addressesLoaderByName(String name){
		return Address.addressesLoaderByName(name, 0);
	}
	public static List<Address> addressesLoaderByName(String name, int excluded_id){
		ArrayList<Address> addresses = new ArrayList<Address>();
		String search = "%"+name+"%";
		String statement = SQLUtils.sqlSafeFormat(ADDRESS_NAME_SEARCH_SQL,search,search,search,excluded_id);
		ResultSet rs = SQLUtils.executeSelect(statement);
		try {
			if(rs==null)
				return addresses;
			while(rs.next()){
				addresses.add(new Address(convertResultSetToAddressMap(rs)));
			}
			rs.close();
		} catch (SQLException e) {
			System.err.println("Strange error here.");
		}
		return addresses;
	}
	/**
	 * Returns the first active address in a list of addresses.
	 * @param addresses The list of addresses
	 * @return The first active address in the list. Null if there isn't one.
	 */
	public static Address getCurrentAddress(List<Address> addresses){
		for (Address a: addresses){
			if (a.active())
				return a;
		}
		return null;
	}
	@Override
	public boolean equals(Object a){
		if(a==null)
			return false;
		if(this==a)
			return true;
		if(a instanceof Address)
			return ((Address) a).id==this.id;
		return false;
	}
}