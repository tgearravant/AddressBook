package net.tullco.addressbook.contact;

import java.util.HashMap;
import java.util.Map;

public class Contact {
	public String firstName;
	public String lastName;
	public String middleName;
	public String street;
	public String zipCode;
	public String city;
	public String state;
	public String country;

	public Contact (Map<String,String> values){
		setValuesFromMap(values);
	}
	public Contact(int id){
		Map<String,String> contact = new HashMap<String,String>();
		if(id==0){
			contact.put("first_name","Tull");
			contact.put("first_name","Gearreald");
			contact.put("middle_name", "Neal");
			contact.put("street", "901 S. Ashland Ave.");
			contact.put("zip_code", "60607");
			contact.put("city", "Chicago");
			contact.put("state", "IL");
			contact.put("country", "US");
		}
		setValuesFromMap(contact);
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
