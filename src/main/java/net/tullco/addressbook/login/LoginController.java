package net.tullco.addressbook.login;

import java.util.Map;

import net.tullco.addressbook.user.UserController;
import net.tullco.addressbook.utils.Path;
import net.tullco.addressbook.utils.ViewUtils;
import spark.*;

public class LoginController {
    public static void ensureUserIsLoggedIn(Request request, Response response) {
        if (request.session().attribute("currentUser") == null) {
            request.session().attribute("loginRedirect", request.pathInfo());
            response.redirect(Path.Web.LOGIN);
        }
    }
    public static Route loginPost = (Request request, Response response) -> {
    	LoginHistory.addLoginHistory(request);
    	Map<String,String> info = ViewUtils.postBodyDecoder(request.body());
    	if(!info.containsKey("username") || !info.containsKey("password"))
    		response.redirect(Path.Web.LOGIN, 303);
    	else{
    		if(UserController.authenticate(info.get("username"), info.get("password"))){
    			response.redirect(getLoginRedirect(request), 303);
    		}else{
    			response.redirect(Path.Web.LOGIN,303);
    		}
    	}
    	return "Redirecting...";
    };
    private static String getLoginRedirect(Request r){
    	String redir=r.session().attribute("loginRedirect");
    	r.session().removeAttribute("loginRedirect");
    	if (redir==null)
    		redir="/";
    	return redir;
    }


}
