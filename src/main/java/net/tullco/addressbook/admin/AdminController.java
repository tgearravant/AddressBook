package net.tullco.addressbook.admin;

import java.util.HashMap;
import java.util.Map;

import net.tullco.addressbook.user.User;
import net.tullco.addressbook.utils.LocaleUtils;
import net.tullco.addressbook.utils.Path;
import net.tullco.addressbook.utils.ViewUtils;
import spark.Request;
import spark.Response;
import spark.Route;
import static spark.Spark.halt;

public class AdminController {
	public static Route adminPost = (Request request, Response response) -> {
		User current_user = User.UserLoader(request.session().attribute("current_user"));
		/*if(current_user == null || !current_user.isAdmin())
			halt(403,"Admin Only");*/
		System.out.println("Loading Admin Post...");
		Map<String,String> options = ViewUtils.postBodyDecoder(request.body());
		System.out.println("LOL2");
		if(options.get("mode").equals("edit_user")){
			System.out.println("LOL");
			User user=User.UserLoader(options.get("username"));
			if (user==null)
				user=User.newUser(options.get("username"), options.get("password"));
			else {
				user.changePassword(options.get("password"));
				user.save();
			}
		}
		else if(options.get("mode").equals("change_password")){
			if(!options.get("password").equals(options.get("confirm_password"))&&options.get("password")!=null){
				response.redirect(Path.Web.CHANGE_PASSWORD+"?password_mismatch=true");
			}
			else{
				current_user.changePassword(options.get("password"));
				current_user.save();
				response.redirect(Path.Web.INDEX);
			}
			return "Redirecting...";
		}
		else if(options.get("mode").equals("add_locale")){
			LocaleUtils.addLocale(options.get("locale"), options.get("long_name"));
			response.redirect(Path.Web.INDEX);
		}
		HashMap<String,Object> model=new HashMap<String,Object>();
		return ViewUtils.render(request, model, Path.Template.EDIT_USER);
	};
	public static Route addUser = (Request request, Response response) -> {
		User current_user = User.UserLoader(request.session().attribute("current_user"));
		if(current_user == null || !current_user.isAdmin())
			halt(403,"Admin Only");
		HashMap<String,Object> model=new HashMap<String,Object>();
		model.put("main_header", "Change Password");
		model.put("header_link", Path.Web.INDEX);
		return ViewUtils.render(request, model, Path.Template.EDIT_USER);
	};
	public static Route changePassword = (Request request, Response response) -> {
		HashMap<String,Object> model=new HashMap<String,Object>();
		model.put("main_header", "Change Password");
		model.put("header_link", Path.Web.INDEX);
		return ViewUtils.render(request,model,Path.Template.CHANGE_PASSWORD);
	};
	public static Route addLocale = (Request request, Response response) -> {
		User current_user = User.UserLoader(request.session().attribute("current_user"));
		HashMap<String,Object> model=new HashMap<String,Object>();
		if(current_user == null || !current_user.isAdmin())
			halt(403,"Admin Only");
		model.put("main_header", "Add Locale");
		model.put("header_link", Path.Web.INDEX);
		return ViewUtils.render(request,model,Path.Template.ADD_LOCALE);
	};
}
