package net.tullco.addressbook.contact;


import java.util.HashMap;

import net.tullco.addressbook.utils.Path;
import net.tullco.addressbook.utils.ViewUtil;
import spark.Request;
import spark.Response;
import spark.Route;
import static spark.Spark.halt;

public class ContactController {
	public static Route displayContact = (Request request, Response response) -> {
		int contact_id;
		
		if (request.params().containsKey(":contact_id")){
        	try{
        		contact_id=Integer.parseInt(request.params(":contact_id"));
        	}catch(NumberFormatException e){
            	halt(404,ViewUtil.renderNotFound(request));
            	return "";
        	}
		}
		else{
			halt(404,ViewUtil.renderNotFound(request));
			return "";
		}
		HashMap<String, Object> model = new HashMap<>();
        Contact contact=Contact.ContactLoader(contact_id);
        if (contact==null){
        	halt(404,ViewUtil.renderNotFound(request));
        }
        model.put("contact", contact);
        return ViewUtil.render(request, model, Path.Template.ONE_CONTACT);
	};
	
/*	public static Route editContact = (Request request, Response response) -> {
        HashMap<String, Object> model = new HashMap<>();
        List<Contact> contacts= new ArrayList<Contact>();
        Contact contact=new Contact(0);
        model.put("contact", contact);
        contacts.add(new Contact(0));
        model.put("contacts", contacts);
        return ViewUtil.render(request, model, Path.Template.INDEX);
	};
	public static Route addContact = (Request request, Response response) -> {
        HashMap<String, Object> model = new HashMap<>();
        List<Contact> contacts= new ArrayList<Contact>();
        Contact contact=new Contact(0);
        model.put("contact", contact);
        contacts.add(new Contact(0));
        model.put("contacts", contacts);
        return ViewUtil.render(request, model, Path.Template.INDEX);
	};
	public static Route deleteContact = (Request request, Response response) -> {
        HashMap<String, Object> model = new HashMap<>();
        List<Contact> contacts= new ArrayList<Contact>();
        Contact contact=new Contact(0);
        model.put("contact", contact);
        contacts.add(new Contact(0));
        model.put("contacts", contacts);
        return ViewUtil.render(request, model, Path.Template.INDEX);
	};*/
}
