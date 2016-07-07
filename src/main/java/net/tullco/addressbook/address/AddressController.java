package net.tullco.addressbook.address;

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

public class AddressController {
	public static Route addAddress = (Request request, Response response) -> {
		System.out.println("Loading the Add Address Page..");
		ViewUtils.haltIfNoParameter(request, ":contact_id", "id");
		int contact_id = Integer.parseInt(request.params(":contact_id"));
		HashMap<String, Object> model = new HashMap<>();
        Contact contact=Contact.ContactLoader(contact_id);
        if (contact==null){
        	halt(404,ViewUtils.renderNotFound(request));
        }
        model.put("contact_id", contact.getId());
        model.put("main_header", "Add Address for "+contact.firstName());
        model.put("mode", "add");
        model.put("street_default", "");
        model.put("apartment_default", "");
        model.put("city_default", "");
        model.put("state_default", "");
        model.put("zip_default", "");
        model.put("active", false);
        model.put("country", "us");
        model.put("locales", LocaleUtils.allowedLocalesList());
        model.put("header_link" , Path.Web.getContactPath(contact_id));
        return ViewUtils.render(request, model, Path.Template.EDIT_ADDRESS);
	};

	public static Route editAddress = (Request request, Response response) -> {
		
		ViewUtils.haltIfNoParameter(request, ":address_id", "id");
		int address_id = Integer.parseInt(request.params(":address_id"));;

		HashMap<String, Object> model = new HashMap<>();
        Address address=Address.addressLoader(address_id);
        if (address==null){
        	halt(404,ViewUtils.renderNotFound(request));
        }
        Contact contact = Contact.ContactLoader(address.contactId());
        model.put("contact_id", address.contactId());
        model.put("address_id",address.id());
        model.put("main_header", "Edit Address for "+(contact.fullName()==null?"":contact.fullName()));
        model.put("mode", "edit");
        model.put("street_default", (address.street()==null?"":address.street()));
        model.put("apartment_default", (address.apartment()==null?"":address.apartment()));
        model.put("city_default", (address.city()==null?"":address.city()));
        model.put("state_default", (address.state()==null?"":address.state()));
        model.put("zip_default", (address.zipCode()==null?"":address.zipCode()));
        model.put("active", address.active());
        model.put("country", address.getLocale());
        model.put("locales", LocaleUtils.allowedLocalesList());
        model.put("header_link" , Path.Web.getContactPath(contact.getId()));
        return ViewUtils.render(request, model, Path.Template.EDIT_ADDRESS);
	};

	public static Route AddressPost = (Request request, Response response) -> {
		System.out.println("Loading Address Post...");
		Map<String,String> options = ViewUtils.postBodyDecoder(request.body());
		String output = "";
		for (String k:options.keySet())
			output+=k+": "+options.get(k)+"\n";
		System.out.println(output);
		if(options.get("mode").equals("add")){
			Address newAddress=new Address(options);
			newAddress.save();
			response.redirect(Path.Web.ONE_CONTACT_NO_ID+newAddress.contactId()+"/",303);
		}
		else if(options.get("mode").equals("edit")){
			options.put("id",options.get("address_id"));
			Address address=new Address(options);
			address.save();
			response.redirect(Path.Web.ONE_CONTACT_NO_ID+address.contactId()+"/",303);
		}
		else if (options.get("mode").equals("delete")){
			Address address = Address.addressLoader(Integer.parseInt(options.get("address_id")));
			response.redirect(Path.Web.ONE_CONTACT_NO_ID+address.contactId()+"/",303);
			address.delete();
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
