package net.tullco.addressbook.contact;


import java.util.Calendar;
import java.util.Date;
//import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.tullco.addressbook.utils.Path;
import net.tullco.addressbook.utils.ViewUtils;
import spark.Request;
import spark.Response;
import spark.Route;
import static spark.Spark.halt;

public class ContactController {
	public static Route displayContact = (Request request, Response response) -> {
		int contact_id;
		ViewUtils.haltIfNoParameter(request, ":contact_id", "int");
		contact_id=Integer.parseInt(request.params(":contact_id"));
		HashMap<String, Object> model = new HashMap<>();
        Contact contact=Contact.ContactLoader(contact_id);
        if (contact==null){
        	halt(404,ViewUtils.renderNotFound(request));
        }
        model.put("contact", contact);
        model.put("main_header", contact.fullName());
        model.put("header_link" , Path.Web.INDEX);
        return ViewUtils.render(request, model, Path.Template.ONE_CONTACT);
	};

	public static Route listContacts = (Request request, Response response) -> {
		HashMap<String, Object> model = new HashMap<>();
        model.put("contacts",Contact.ContactsLoader("", 50, 0));
        return ViewUtils.render(request, model, Path.Template.LIST_CONTACTS);
	};

	public static Route searchContacts = (Request request, Response response) -> {
		HashMap<String, Object> model = new HashMap<>();
		if (request.params().containsKey(":search"))
		response.redirect(Path.Web.INDEX);
        model.put("contacts",Contact.ContactsLoader("", 50, 0));
        return ViewUtils.render(request, model, Path.Template.LIST_CONTACTS);
	};
	
	public static Route contactSearchPost = (Request request, Response response) -> {
		HashMap<String, Object> model = new HashMap<>();
        model.put("contacts",Contact.ContactsLoader("", 50, 0));
        return ViewUtils.render(request, model, Path.Template.LIST_CONTACTS);
	}; 

	public static Route editContact = (Request request, Response response) -> {
        HashMap<String, Object> model = new HashMap<>();
		ViewUtils.haltIfNoParameter(request, ":contact_id", "int");
		Contact c=Contact.ContactLoader(Integer.parseInt(request.params(":contact_id")));
		if (c==null)
			halt(404,ViewUtils.renderNotFound(request));
		Calendar cal=Calendar.getInstance();
<<<<<<< HEAD
		cal.setTime(c.birthdate());
=======
		cal.setTime(c.birthday());
>>>>>>> refs/remotes/origin/contact_features
        model.put("mode", "edit");
        model.put("contact_id", c.getId());
        model.put("first_name", c.firstName());
        model.put("last_name", c.lastName());
        model.put("middle_name",c.middleName());
        model.put("email", c.email());
        model.put("birthday", cal.get(Calendar.DATE));
        model.put("birthmonth", cal.get(Calendar.MONTH)+1);
        model.put("birthyear", cal.get(Calendar.YEAR));
        model.put("header_link" , Path.Web.getContactPath(c.getId()));
        
        return ViewUtils.render(request, model, Path.Template.EDIT_CONTACTS);
	};
	public static Route addContact = (Request request, Response response) -> {
        HashMap<String, Object> model = new HashMap<>();
		Calendar cal=Calendar.getInstance();
		cal.setTime(new Date());
        model.put("mode", "add");
<<<<<<< HEAD
        model.put("id", "");
        model.put("first_name", "");
        model.put("last_name", "");
        model.put("middle_name","");
        model.put("email", "");
        model.put("birthday", cal.get(Calendar.DAY_OF_MONTH));
        model.put("birthmonth",cal.get(Calendar.MONTH)+1);
        model.put("birthyear", cal.get(Calendar.YEAR)-26);
        model.put("header_link" , Path.Web.INDEX);
=======
        model.put("first_name", "");
        model.put("last_name", "");
        model.put("middle_name","");
        model.put("birthday", 1);
        model.put("birthmonth", 1);
        model.put("birthyear", cal.get(Calendar.YEAR));
>>>>>>> refs/remotes/origin/contact_features
        
        return ViewUtils.render(request, model, Path.Template.EDIT_CONTACTS);
	};
	public static Route contactPost = (Request request, Response response) -> {
<<<<<<< HEAD
		System.out.println("Loading Contact Post...");
		Map<String,String> options = ViewUtils.postBodyDecoder(request.body());
		String output = "";
		for (String k:options.keySet())
			output+=k+": "+options.get(k)+"\n";
		System.out.println(output);
		if(options.get("mode").equals("add")){
			Contact newContact = new Contact(options);
			newContact.save();
			response.redirect(Path.Web.ONE_CONTACT_NO_ID+newContact.getId() +"/",303);
		}
		else if(options.get("mode").equals("edit")){
			options.put("id",options.get("contact_id"));
			Contact c = new Contact(options);
			c.save();
			response.redirect(Path.Web.ONE_CONTACT_NO_ID+c.getId()+"/",303);
		}
		else if (options.get("mode").equals("delete")){
			Contact c = Contact.ContactLoader(Integer.parseInt(options.get("contact_id")));
			response.redirect(Path.Web.INDEX,303);
			c.delete();
		}
		else{
			if (options.containsKey("contact_id"))
				response.redirect(Path.Web.ONE_CONTACT_NO_ID+options.get("contact_id"));
			else
				response.redirect(Path.Web.INDEX,303);
		}
        return "Redirecting back to contact...";
=======
		ViewUtils.postBodyDecoder(request.body());
		
		return "Redirecting to list...";
>>>>>>> refs/remotes/origin/contact_features
	};
	/*
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
