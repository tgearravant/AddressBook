package net.tullco.addressbook;

import static spark.Spark.*;
import net.tullco.addressbook.address.AddressController;
import net.tullco.addressbook.contact.ContactController;
import net.tullco.addressbook.login.LoginController;
import net.tullco.addressbook.phone_number.PhoneNumberController;
import net.tullco.addressbook.user.UserController;
import net.tullco.addressbook.utils.*;
import spark.servlet.SparkApplication;

import static spark.debug.DebugScreen.*;

/**
 * Hello world!
 *
 */
public class App implements SparkApplication
{
    public static void main( String[] args ){
    	initialConfiguration();
    	beforeFilters();
    	getRouting();
    	postRouting();
    	//after();
        System.out.println(System.getProperty("os.name"));
    }
    public void init(){
    	initialConfiguration();
    	beforeFilters();
    	getRouting();
    	postRouting();
    	//after();
        System.out.println(System.getProperty("os.name"));
    	
    }
    /**
     * Runs database migrations, sets up the server, sets the static
     * file location, confirms that required properties are set,
     * creates admin user, and enables debugging if necessary.
     */
    private static void initialConfiguration(){
    	SQLUtils.runMigrations();
    	SystemUtils.checkForRequiredProperties();
    	UserController.createAdmin(SystemUtils.getProperty("admin_username"), SystemUtils.getProperty("admin_password"));
    	staticFiles.location(SystemUtils.adjustPathForOS("/public"));
    	port(Integer.parseInt(SystemUtils.getProperty("port", "4567")));
    	if (SystemUtils.inProduction()){
    		
    	}
    	else{
    		enableDebugScreen();
    	}
    	
    }
    private static void beforeFilters(){
    	before("*",Filters.addTrailingSlashes);
    	before("*",Filters.ensureLoggedIn);
    }
    private static void getRouting(){
    	//Index Routing
    	get(Path.Web.INDEX,				ContactController.listContacts);
    	get(Path.Web.ONE_CONTACT,		ContactController.displayContact);
    	get(Path.Web.ADD_ADDRESS,		AddressController.addAddress);
    	get(Path.Web.EDIT_ADDRESS,		AddressController.editAddress);
    	get(Path.Web.LOGIN,				LoginController.login);
    	get(Path.Web.LOGOUT,			LoginController.logout);
    	get(Path.Web.ADD_PHONE_NUMBER,	PhoneNumberController.addPhoneNumber);
    	get(Path.Web.EDIT_PHONE_NUMBER,	PhoneNumberController.editPhoneNumber);
    	
    	//404 Routing
    	get("*",						ViewUtils.notFound);
    }
    private static void postRouting(){
    	post(Path.Web.SEARCH_POST,		ContactController.searchContacts);
    	post(Path.Web.ADDRESS_POST,		AddressController.AddressPost);
    	post(Path.Web.PHONE_NUMBER_POST,PhoneNumberController.phoneNumberPost);
    	post(Path.Web.LOGIN_POST,		LoginController.loginPost);
    	
    	//404 Routing
    	post("*",						ViewUtils.notFound);
    }
}
