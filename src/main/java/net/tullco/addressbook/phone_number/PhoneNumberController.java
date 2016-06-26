package net.tullco.addressbook.phone_number;

import java.util.Map;

import net.tullco.addressbook.utils.Path;
import net.tullco.addressbook.utils.ViewUtils;
import spark.Request;
import spark.Response;
import spark.Route;

public class PhoneNumberController {
	public static Route phoneNumberPost = (Request request, Response response) -> {
		System.out.println("Loading Phone Number Post...");
		Map<String,String> options = ViewUtils.postBodyDecoder(request.body());
		String output = "";
		for (String k:options.keySet())
			output+=k+": "+options.get(k)+"\n";
		System.out.println(output);
		PhoneNumber newNumber=new PhoneNumber(options);
		newNumber.save();
		response.redirect(Path.Web.ONE_CONTACT_NO_ID+newNumber.getContactId()+"/",303);
        return "Redirecting back to contact...";
	};
}
