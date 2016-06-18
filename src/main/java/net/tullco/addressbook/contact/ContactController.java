package net.tullco.addressbook.contact;

import spark.Request;
import spark.Response;
import spark.Route;

public class ContactController {
	public static Route displayContact = (Request request, Response response) -> "HelloWorld";
}
