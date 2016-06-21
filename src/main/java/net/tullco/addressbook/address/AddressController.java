package net.tullco.addressbook.address;

import static spark.Spark.halt;

import java.util.HashMap;

import net.tullco.addressbook.contact.Contact;
import net.tullco.addressbook.utils.Path;
import net.tullco.addressbook.utils.ViewUtil;
import spark.Request;
import spark.Response;
import spark.Route;

public class AddressController {
	public static Route addAddress = (Request request, Response response) -> {
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
        model.put("contact_id", contact.getId());
        model.put("main_header", contact.fullName());
        model.put("mode", "add");
        return ViewUtil.render(request, model, Path.Template.EDIT_ADDRESS);
	};

	public static Route editAddress = (Request request, Response response) -> {
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
        model.put("main_header", contact.fullName());
        model.put("mode", "edit");
        return ViewUtil.render(request, model, Path.Template.EDIT_ADDRESS);
	};

	public static Route AddressPost = (Request request, Response response) -> {
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
        model.put("main_header", contact.fullName());
        model.put("mode", "edit");
        return ViewUtil.render(request, model, Path.Template.EDIT_ADDRESS);
	};
}
