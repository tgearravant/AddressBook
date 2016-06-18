package net.tullco.addressbook.contact;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.tullco.addressbook.utils.Path;
import net.tullco.addressbook.utils.ViewUtil;
import spark.Request;
import spark.Response;
import spark.Route;

public class ContactController {
	public static Route displayContact = (Request request, Response response) -> {
        HashMap<String, Object> model = new HashMap<>();
        //List<Contact> contacts= new ArrayList<Contact>();
        Contact contact=new Contact(0);
        //contacts.add(new Contact(0));
        model.put("contact", contact);
        return ViewUtil.render(request, model, Path.Template.INDEX);
	};
}
