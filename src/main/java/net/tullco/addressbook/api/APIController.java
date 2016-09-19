package net.tullco.addressbook.api;

import java.util.List;
import java.util.Map;

import net.tullco.addressbook.contact.Contact;
import net.tullco.addressbook.user.User;
import net.tullco.addressbook.utils.ViewUtils;
import spark.Request;
import spark.Response;
import spark.Route;
import org.json.*;

public class APIController {
	public static Route postAPIKey= (Request request, Response response) -> {
		System.out.println("API Auth Attempt...");
		Map<String,String> options = ViewUtils.postBodyDecoder(request.body());
		User attemptedUser = User.userLoader(options.get("username"));
		String apiKey=null;
		if (attemptedUser != null && attemptedUser.checkPassword(options.get("password"))){
			apiKey=attemptedUser.getAPIKey();
		}
		JSONObject json = new JSONObject();
		json.put("api", "authentication");
		if (apiKey != null){
			JSONObject userJson = new JSONObject();
			userJson.put("username", attemptedUser.getUsername());
			userJson.put("api_key", apiKey);
			json.put("user_info",userJson);
		}
		else{
			JSONObject errorJson = new JSONObject();
			errorJson.put("error_message", "Invalid Credentials");
			json.put("errors", errorJson);
		}
		response.type("application/json");
		return json.toString();
	};
	public static Route postAPIGetData  = (Request request, Response response) -> {
		System.out.println("Data Sync Request...");
		Map<String,String> options = ViewUtils.postBodyDecoder(request.body());
		JSONObject json = new JSONObject();
		json.put("api","data_sync");
		if(!options.containsKey("api_key")){
			JSONObject errorJson = new JSONObject();
			errorJson.put("error_message", "No Key");
			json.put("errors", errorJson);
		}
		else if (APIController.validKey(options.get("api_key"))){
			List<Contact> contacts = Contact.ContactsLoader("", 10000, 0);
			for(Contact c: contacts){
				json.append("contacts", c.toJSON());
			}
		}else{
			JSONObject errorJson = new JSONObject();
			errorJson.put("error_message", "Invalid Key");
			json.put("errors", errorJson);
		}
		response.type("application/json");
		return json.toString();
	};
	private static boolean validKey(String s){
		User u = User.userLoaderAPI(s);
		if (u==null)
			return false;
		return true;
	}
}
