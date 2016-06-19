package net.tullco.addressbook.address;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tullco.addressbook.utils.SQLiteUtils;

public class Address {
	private int id;
	private String street;
	private String apartment;
	private String zipCode;
	private String city;
	private String state;
	private String country;
	private boolean active;

	private static final String ACTIVE_ADDRESS_LOADER_SQL="SELECT id,active,street,apartment,zip_code,city,state,country FROM addresses WHERE contact_id=%d AND active=1 LIMIT 1";
	private static final String ADDRESSES_LOADER_SQL="SELECT id,active,street,apartment,zip_code,city,state,country FROM addresses WHERE contact_id=%d";
	private static final String ADDRESS_LOADER_SQL="SELECT id,active,street,apartment,zip_code,city,state,country FROM addresses WHERE id=%d";

	public Address(Map<String,String> values){
		setValuesFromMap(values);
	}
	
	public int id(){
		return this.id;
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
	public static Address addressLoader(int address_id){
		String statement = String.format(ADDRESS_LOADER_SQL, address_id);
		ResultSet rs = SQLiteUtils.executeSelect(statement);
		try {
			if(!rs.isBeforeFirst())
				return null;
			return new Address(convertResultSetToAddressMap(rs));
		} catch (SQLException e) {
			return null;
		}
	}
	public static Address activeAddressLoader(int contact_id){
		String statement = String.format(ACTIVE_ADDRESS_LOADER_SQL, contact_id);
		ResultSet rs = SQLiteUtils.executeSelect(statement);
		try {
			if(!rs.isBeforeFirst()){
				List<Address> addresses=AddressesLoader(contact_id);
				if (addresses.size()==0)
					return null;
				else
					return AddressesLoader(contact_id).get(0);
			}
			return new Address(convertResultSetToAddressMap(rs));
		} catch (SQLException e) {
			return null;
		}
	}
	public static List<Address> AddressesLoader(int contact_id){
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
	private static Map<String,String> convertResultSetToAddressMap(ResultSet rs) throws SQLException{
		String[] fields={"street","apartment","zip_code","city","state","country"};
		HashMap<String,String> contact=new HashMap<String, String>();
		for(String s:fields){
			contact.put(s, rs.getString(s));
		}
		contact.put("active", Integer.toString(rs.getInt("active")));
		contact.put("id", Integer.toString(rs.getInt("id")));
		return contact;
	}
	private void setValuesFromMap(Map<String,String> values){
		for (String k: values.keySet()){
			if(k.equals("street"))
				this.street = values.get(k);
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
				this.active=Boolean.parseBoolean(values.get(k));
		}
	}
}
