package net.tullco.addressbook.address;

import static spark.Spark.halt;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Set;

import net.tullco.addressbook.contact.Contact;
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
        model.put("main_header", "Add Address");
        model.put("mode", "edit");
        model.put("street_default", address.street());
        model.put("apartment_default", address.apartment());
        model.put("city_default", address.city());
        model.put("state_default", address.state());
        model.put("zip_default", address.zipCode());
        return ViewUtils.render(request, model, Path.Template.EDIT_ADDRESS);
	};

	public static Route AddressPost = (Request request, Response response) -> {
		System.out.println("Loading Address Post...");
		String output = "";
		ViewUtils.postBodyDecoder(request.body());
		
        return output;
        //String ummm="Well... That shouldn't have happened... Oops, I guess?";
        //System.out.println(ummm);
        //return ummm;
	};
}
