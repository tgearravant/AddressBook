package net.tullco.addressbook.utils;


import static spark.Spark.halt;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import net.tullco.addressbook.utils.Path;

import org.apache.velocity.app.*;
import org.eclipse.jetty.http.HttpStatus;

import spark.template.velocity.*;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;

public class ViewUtils {
    public static String render(Request request, Map<String, Object> model, String templatePath) {
        model.put("WebPath", Path.Web.class); // Access application URLs from templates
        model.put("title", "Tull & Beverly's Address Book");
        model.put("display_utils_class", DisplayUtils.class);
        if (!model.containsKey("main_header")){
        	model.put("main_header", "Tull & Beverly's Address Book");
        }
        return strictVelocityEngine().render(new ModelAndView(model, templatePath));
    }
    private static VelocityTemplateEngine strictVelocityEngine() {
        VelocityEngine configuredEngine = new VelocityEngine();
        configuredEngine.setProperty("runtime.references.strict", true);
        configuredEngine.setProperty("resource.loader", "class");
        configuredEngine.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        return new VelocityTemplateEngine(configuredEngine);
    }
    public static Route notFound = (Request request, Response response) -> {
        response.status(HttpStatus.NOT_FOUND_404);
        System.out.println("404");
        return renderNotFound(request);
    };
    public static String renderNotFound(Request request){
    	return render(request, new HashMap<>(), Path.Template.NOT_FOUND);
    }
    public static void haltIfNoParameter(Request request, String param,String type){
		if (request.params().containsKey(param)){
        	if (type=="id"){
				try{
	        		Integer.parseInt(request.params(param));
	        	}catch(NumberFormatException e){
	            	halt(404,ViewUtils.renderNotFound(request));
	        	}
        	}
		}
		else{
			halt(404,ViewUtils.renderNotFound(request));
		}
    }
    public static Map<String,String> postBodyDecoder(String body){
    	HashMap<String,String> decodedMap=new HashMap<String,String>();
    	for (String param: body.split("\\&")){
    		String[] keyVal=param.split("\\=");
			try {
				decodedMap.put(keyVal[0], URLDecoder.decode(keyVal[1],"UTF-8"));
			} catch (UnsupportedEncodingException e) {
				decodedMap.put(keyVal[0], null);
			} catch (ArrayIndexOutOfBoundsException e){
				decodedMap.put(keyVal[0], null);
			}
    	}
    	return decodedMap;
    }
}
