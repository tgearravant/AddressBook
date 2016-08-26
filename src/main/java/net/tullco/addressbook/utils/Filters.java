package net.tullco.addressbook.utils;

import net.tullco.addressbook.login.LoginController;
import spark.Filter;
import spark.Request;
import spark.Response;


public class Filters {
	
	/**
	 * The suffixes that will not have a / added after them in URLS.
	 */
	public static final String[] ALLOWED_SUFFIXES = {".vcf"};
	
    public static Filter addTrailingSlashes = (Request request, Response response) -> {
    	String path = request.pathInfo();
        if (!path.endsWith("/")) {
        	for(String suffix : ALLOWED_SUFFIXES)
        		if(path.endsWith(suffix))
        			return;
            response.redirect(request.pathInfo() + "/");
        }
    };
    
    public static Filter ensureLoggedIn = (Request request, Response response) -> {
    	if(!Path.Web.getUnprotectedPaths().contains(request.pathInfo())){
    		LoginController.ensureUserIsLoggedIn(request, response);
    	}
    };

}
