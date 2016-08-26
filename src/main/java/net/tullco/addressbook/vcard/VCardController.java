package net.tullco.addressbook.vcard;

import net.tullco.addressbook.contact.Contact;
import net.tullco.addressbook.utils.ViewUtils;
import spark.Route;

public class VCardController {
	public static Route downloadVCard = (request,response) -> {
		ViewUtils.haltIfNoParameter(request, ":contact_id", "string");
		int contact_id = Integer.parseInt(request.params(":contact_id"));
		response.type("text/vcard");
		return new VCard(Contact.contactLoader(contact_id)).toString();
	};
}
