package net.tullco.addressbook.api;

import java.util.Map;

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
		User attemptedUser = User.UserLoader(options.get("username"));
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
}
