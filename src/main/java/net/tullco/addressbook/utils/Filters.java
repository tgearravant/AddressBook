package net.tullco.addressbook.utils;

import net.tullco.addressbook.login.LoginController;
import spark.Filter;
import spark.Request;
import spark.Response;


public class Filters {

    public static Filter addTrailingSlashes = (Request request, Response response) -> {
        if (!request.pathInfo().endsWith("/")) {
            response.redirect(request.pathInfo() + "/");
        }
    };
    
    public static Filter ensureLoggedIn = (Request request, Response response) -> {
    	if(!Path.Web.getUnprotectedPaths().contains(request.pathInfo())){
    		LoginController.ensureUserIsLoggedIn(request, response);
    	}
    };

}
