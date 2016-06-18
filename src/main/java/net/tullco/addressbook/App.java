package net.tullco.addressbook;

import static spark.Spark.*;

import net.tullco.addressbook.contact.ContactController;
import net.tullco.addressbook.utils.*;
import static spark.debug.DebugScreen.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	initialConfiguration();
    	before();
    	getRouting();
    	//after();
        System.out.println(System.getProperty("os.name"));
        get("/hello",(req,res) -> "Hello World");
    }
    private static void initialConfiguration(){
    	staticFiles.location(SystemUtils.adjustPathForOS("/public"));
    	if (SystemUtils.inProduction())
    		port(80);
    	else
    		port(4567);
    		enableDebugScreen(); 
    	
    }
    private static void getRouting(){
    	//Test Page
    	get("/hello",(req,res) -> "Hello World");
    	//Index Routing
    	get(Path.Web.INDEX,          ContactController.displayContact);
    }
    private static void before(){
    }
}
