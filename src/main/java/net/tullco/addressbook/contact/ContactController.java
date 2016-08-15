package net.tullco.addressbook.contact;


import java.util.Calendar;
import java.util.Date;
//import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.tullco.addressbook.utils.DisplayUtils;
import net.tullco.addressbook.utils.Path;
import net.tullco.addressbook.utils.SQLUtils;
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
        model.put("main_header", "Details for "+(contact.fullName().isEmpty()?"unnamed contact":contact.fullName()));
        model.put("header_link" , Path.Web.INDEX);
        model.put("contact_id", contact.getId());
        return ViewUtils.render(request, model, Path.Template.ONE_CONTACT);
	};

	public static Route listContacts = (Request request, Response response) -> {
		HashMap<String, Object> model = new HashMap<>();
        model.put("contacts",Contact.ContactsLoader("", 50, 0));
        model.put("add_contact", "yes");
        return ViewUtils.render(request, model, Path.Template.LIST_CONTACTS);
	};

	public static Route searchContacts = (Request request, Response response) -> {
		String search="%"+request.params(":search")+"%";
		String whereStatement="AND (lower(first_name) LIKE lower(%s) OR lower(last_name) LIKE lower(%s))";
		String safeWhereStatement=SQLUtils.sqlSafeFormat(whereStatement,search, search);
		System.out.println(safeWhereStatement);
		HashMap<String, Object> model = new HashMap<>();
		ViewUtils.haltIfNoParameter(request, ":search", "string");
		model.put("contacts",Contact.ContactsLoader(safeWhereStatement, 50, 0));
        model.put("add_contact", "yes");
        model.put("header_link", Path.Web.INDEX);
        model.put("main_header", "Search Results for: "+search.substring(1, search.length()-1));
        return ViewUtils.render(request, model, Path.Template.LIST_CONTACTS);
	};
	
	public static Route contactSearchPost = (Request request, Response response) -> {
		Map<String,String> map = ViewUtils.postBodyDecoder(request.body());
		if (map.get("search")==null||map.get("search").isEmpty())
			response.redirect(Path.Web.INDEX);
		else{
			String searchRedirect = Path.Web.SEARCH_RESULTS.replace(":search", map.get("search").trim());
			response.redirect(searchRedirect, 303);
		}
        return "Redirecting to search results...";
	}; 

	public static Route editContact = (Request request, Response response) -> {
        HashMap<String, Object> model = new HashMap<>();
		ViewUtils.haltIfNoParameter(request, ":contact_id", "int");
		Contact c=Contact.ContactLoader(Integer.parseInt(request.params(":contact_id")));
		if (c==null)
			halt(404,ViewUtils.renderNotFound(request));
		System.out.println("Birthday: "+DisplayUtils.dateToString(c.birthdate()));
        model.put("mode", "edit");
        model.put("contact_id", c.getId());
        model.put("first_name", (c.firstName()==null?"":c.firstName()));
        model.put("last_name", (c.lastName()==null?"":c.lastName()));
        model.put("middle_name",(c.middleName()==null?"":c.middleName()));
        model.put("email", (c.email()==null?"":c.email()));
        if (c.birthdate()!=null){
    		Calendar cal=Calendar.getInstance();
    		cal.setTime(c.birthdate());
	        model.put("birthday", cal.get(Calendar.DATE));
	        model.put("birthmonth", cal.get(Calendar.MONTH)+1);
	        model.put("birthyear", cal.get(Calendar.YEAR));
        }else{
            model.put("birthday", "");
            model.put("birthmonth", "");
            model.put("birthyear", "");
        }
        model.put("header_link" , Path.Web.getContactPath(c.getId()));
        model.put("main_header", "Edit Contact "+(c.fullName()==null?"":c.fullName()));
        
        return ViewUtils.render(request, model, Path.Template.EDIT_CONTACTS);
	};
	public static Route addContact = (Request request, Response response) -> {
        HashMap<String, Object> model = new HashMap<>();
		Calendar cal=Calendar.getInstance();
		cal.setTime(new Date());
        model.put("mode", "add");
        model.put("id", "");
        model.put("first_name", "");
        model.put("last_name", "");
        model.put("middle_name","");
        model.put("email", "");
        model.put("birthday", "");
        model.put("birthmonth", "");
        model.put("birthyear", "");
        model.put("header_link" , Path.Web.INDEX);
        model.put("main_header", "Add Contact");
        
        return ViewUtils.render(request, model, Path.Template.EDIT_CONTACTS);
	};
	public static Route contactPost = (Request request, Response response) -> {
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
	};
}
