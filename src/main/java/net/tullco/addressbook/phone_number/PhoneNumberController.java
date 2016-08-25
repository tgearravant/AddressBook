package net.tullco.addressbook.phone_number;

import static spark.Spark.halt;

import java.util.HashMap;
import java.util.Map;

import net.tullco.addressbook.contact.Contact;
import net.tullco.addressbook.utils.LocaleUtils;
import net.tullco.addressbook.utils.Path;
import net.tullco.addressbook.utils.ViewUtils;
import spark.Request;
import spark.Response;
import spark.Route;

public class PhoneNumberController {
	
	public static Route addPhoneNumber = (Request request, Response response) -> {
		System.out.println("Loading Add Phone Number...");
		ViewUtils.haltIfNoParameter(request, ":contact_id", "id");
		int contact_id = Integer.parseInt(request.params(":contact_id"));

		HashMap<String, Object> model = new HashMap<>();
        Contact c = Contact.contactLoader(contact_id);
        if (c==null){
        	halt(404,ViewUtils.renderNotFound(request));
        }
        model.put("contact_id", contact_id);
        model.put("main_header", "Add Phone Number for "+c.fullName());
        model.put("mode", "add");
        model.put("number", "");
        model.put("type", "");
        model.put("locale", "");
        model.put("preferred", true);
        model.put("types", PhoneNumber.getAllowedTypes());
        model.put("locales", LocaleUtils.allowedLocalesList());
        model.put("header_link" , Path.Web.getContactPath(contact_id));
        return ViewUtils.render(request, model, Path.Template.EDIT_PHONE_NUMBER);
	};
	
	public static Route editPhoneNumber = (Request request, Response response) -> {
		System.out.println("Loading Edit Phone Number...");
		
		ViewUtils.haltIfNoParameter(request, ":phone_number_id", "id");
		int phone_number_id = Integer.parseInt(request.params(":phone_number_id"));;

		HashMap<String, Object> model = new HashMap<>();
        PhoneNumber pn = PhoneNumber.phoneNumberLoader(phone_number_id);
        if (pn==null){
        	halt(404,ViewUtils.renderNotFound(request));
        }
        Contact contact = Contact.contactLoader(pn.getContactId());
        model.put("contact_id", pn.getContactId());
        model.put("phone_number_id",pn.getId());
        model.put("main_header", "Edit Phone Number for "+contact.fullName());
        model.put("mode", "edit");
        model.put("number", pn.getNumber());
        model.put("type", pn.getType());
        model.put("locale", pn.getLocale());
        model.put("preferred", pn.isPreferred());
        model.put("types", PhoneNumber.getAllowedTypes());
        model.put("locales", LocaleUtils.allowedLocalesList());
        model.put("header_link" , Path.Web.getContactPath(contact.getId()));
        return ViewUtils.render(request, model, Path.Template.EDIT_PHONE_NUMBER);
	};
	
	public static Route phoneNumberPost = (Request request, Response response) -> {
		System.out.println("Loading Phone Number Post...");
		Map<String,String> options = ViewUtils.postBodyDecoder(request.body());
		String output = "";
		for (String k:options.keySet())
			output+=k+": "+options.get(k)+"\n";
		System.out.println(output);
		if(options.get("mode").equals("add")){
			PhoneNumber newNumber=new PhoneNumber(options);
			newNumber.save();
			response.redirect(Path.Web.ONE_CONTACT_NO_ID+newNumber.getContactId()+"/",303);
		}
		if(options.get("mode").equals("edit")){
			options.put("id", options.get("phone_number_id"));
			PhoneNumber newNumber=new PhoneNumber(options);
			newNumber.save();
			response.redirect(Path.Web.ONE_CONTACT_NO_ID+newNumber.getContactId()+"/",303);
		}
		if(options.get("mode").equals("delete")){
			PhoneNumber pn=PhoneNumber.phoneNumberLoader(Integer.parseInt(options.get("phone_number_id")));
			int contact_id=pn.getContactId();
			pn.delete();
			response.redirect(Path.Web.ONE_CONTACT_NO_ID+contact_id+"/",303);
		}
        return "Redirecting back to contact...";
	};
}
