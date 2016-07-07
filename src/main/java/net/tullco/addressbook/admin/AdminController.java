package net.tullco.addressbook.admin;

import java.util.HashMap;
import java.util.Map;

import net.tullco.addressbook.user.User;
import net.tullco.addressbook.utils.Path;
import net.tullco.addressbook.utils.ViewUtils;
import spark.Request;
import spark.Response;
import spark.Route;
import static spark.Spark.halt;

public class AdminController {
	public static Route adminPost = (Request request, Response response) -> {
		User current_user = User.UserLoader(request.session().attribute("current_user"));
		if(current_user == null || !current_user.isAdmin())
			halt(403,"Admin Only");
		System.out.println("Loading Admin Post...");
		Map<String,String> options = ViewUtils.postBodyDecoder(request.body());
		if(options.get("mode").equals("edit_user")){
			User user=User.UserLoader(options.get("username"));
			if (user==null)
				user=User.newUser(options.get("username"), options.get("password"));
			else {
				user.changePassword(options.get("password"));
				user.save();
			}
		}
		HashMap<String,Object> model=new HashMap<String,Object>();
		return ViewUtils.render(request, model, Path.Template.EDIT_USER);
	};
	public static Route addUser = (Request request, Response response) -> {
		User current_user = User.UserLoader(request.session().attribute("current_user"));
		if(current_user == null || !current_user.isAdmin())
			halt(403,"Admin Only");
		HashMap<String,Object> model=new HashMap<String,Object>();
		return ViewUtils.render(request, model, Path.Template.EDIT_USER);
	};
	public static Route changePassword = (Request request, Response response) -> {
		HashMap<String,Object> model=new HashMap<String,Object>();
    	model.put("main_header", "Change Password");
    	model.put("header_link", Path.Web.INDEX);
    	return ViewUtils.render(request,model,Path.Template.CHANGE_PASSWORD);
	};
}
