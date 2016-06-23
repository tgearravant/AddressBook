package net.tullco.addressbook;

import static spark.Spark.*;
import net.tullco.addressbook.address.AddressController;
import net.tullco.addressbook.contact.ContactController;
import net.tullco.addressbook.phone_number.PhoneNumberController;
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
    	postRouting();
    	//after();
        System.out.println(System.getProperty("os.name"));
        get("/hello",(req,res) -> "Hello World");
    }
    private static void initialConfiguration(){
    	SQLiteUtils.runMigrations();
    	staticFiles.location(SystemUtils.adjustPathForOS("/public"));
    	if (SystemUtils.inProduction())
    		port(80);
    	else
    		port(4567);
    		enableDebugScreen();
    	
    }
    private static void getRouting(){
    	//Index Routing
    	get(Path.Web.INDEX,				ContactController.listContacts);
    	get(Path.Web.ONE_CONTACT,		ContactController.displayContact);
    	get(Path.Web.ADD_ADDRESS,		AddressController.addAddress);
    	get(Path.Web.EDIT_ADDRESS,		AddressController.editAddress);
    	
    	//404 Routing
    	get("*",						ViewUtils.notFound);
    }
    private static void postRouting(){
    	post(Path.Web.SEARCH_POST,		ContactController.searchContacts);
    	post(Path.Web.ADDRESS_POST,		AddressController.AddressPost);
    	post(Path.Web.PHONE_NUMBER_POST,PhoneNumberController.phoneNumberPost);
    	
    	//404 Routing
    	post("*",						ViewUtils.notFound);
    }
    private static void before(){
    }
}
