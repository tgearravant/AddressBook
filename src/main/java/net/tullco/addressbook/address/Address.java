package net.tullco.addressbook.address;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tullco.addressbook.utils.SQLiteUtils;

public class Address {
	private int id=0;
	private int contact_id=0;
	private String street;
	private String apartment;
	private String zipCode;
	private String city;
	private String state;
	private String country;
	private boolean active;

	private static final String ADDRESSES_LOADER_SQL="SELECT * FROM addresses WHERE contact_id=%d";
	private static final String ADDRESS_LOADER_SQL="SELECT * FROM addresses WHERE id=%d";
	private static final String ADDRESS_INSERT_SQL="INSERT INTO addresses "
			+ "(contact_id,street,apartment,zip_code,city,state,country,active) "
			+ "VALUES (%d,%s,%s,%s,%s,%s,%s,%d)";
	
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
			String statement=String.format(ADDRESS_INSERT_SQL,this.contact_id
					,this.street
					,this.apartment
					,this.zipCode
					,this.city
					,this.state
					,this.country
					,(this.active?1:0));
			System.out.println(statement);
			//SQLiteUtils.executeInsert();
		}
		return true;
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
	public String country(){
		return this.country;
	}
	public boolean active(){
		return this.active;
	}
	private static Map<String,String> convertResultSetToAddressMap(ResultSet rs) throws SQLException{
		String[] fields={"street","apartment","zip_code","city","state","country"};
		HashMap<String,String> contact=new HashMap<String, String>();
		for(String s:fields){
			contact.put(s, rs.getString(s));
		}
		contact.put("active", Integer.toString(rs.getInt("active")));
		contact.put("id", Integer.toString(rs.getInt("id")));
		contact.put("contact_id", Integer.toString(rs.getInt("contact_id")));
		return contact;
	}

	public static Address addressLoader(int address_id){
		String statement = String.format(ADDRESS_LOADER_SQL, address_id);
		ResultSet rs = SQLiteUtils.executeSelect(statement);
		try {
			if(!rs.isBeforeFirst())
				return null;
			rs.next();
			return new Address(convertResultSetToAddressMap(rs));
		} catch (SQLException e) {
			return null;
		}
	}
	public static List<Address> addressesLoader(int contact_id){
		String statement = String.format(ADDRESSES_LOADER_SQL, contact_id);
		ResultSet rs = SQLiteUtils.executeSelect(statement);
		ArrayList<Address> addresses = new ArrayList<Address>();
		try {
			while(rs.next()){
				addresses.add(new Address(convertResultSetToAddressMap(rs)));
			}
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
		Address temp=new Address(valueMap);
		temp.save();
		return temp;
	}
}
