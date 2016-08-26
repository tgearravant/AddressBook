package net.tullco.addressbook;

import static spark.Spark.*;
import net.tullco.addressbook.address.AddressController;
import net.tullco.addressbook.admin.AdminController;
import net.tullco.addressbook.contact.ContactController;
import net.tullco.addressbook.login.LoginController;
import net.tullco.addressbook.phone_number.PhoneNumberController;
import net.tullco.addressbook.user.UserController;
import net.tullco.addressbook.utils.*;
import net.tullco.addressbook.vcard.VCardController;
import spark.servlet.SparkApplication;

import static spark.debug.DebugScreen.*;


/**
 * This contains all the routing and stuff. :)
 *
 */
public class App implements SparkApplication
{
	/**
	 * The main method that is used to start the server in development.
	 * @param args Leave empty. Not used.
	 */
    public static void main( String[] args ) {
    	initialConfiguration();
    	beforeFilters();
    	getRouting();
    	postRouting();
    	//after();
        //System.out.println(System.getProperty("os.name"));
    }
    /**
     * The non-static init function for use in the production deploy.
     */
    public void init(){
    	initialConfiguration();
    	beforeFilters();
    	getRouting();
    	postRouting();
    	//after();
        //System.out.println(System.getProperty("os.name"));
    }
    /**
     * Runs database migrations, sets up the server, sets the static
     * file location, confirms that required properties are set,
     * creates admin user, and enables debugging if not in production.
     */
    private static void initialConfiguration(){
    	SQLUtils.runMigrations();
    	SystemUtils.checkForRequiredProperties();
    	if(!SystemUtils.inTesting())
    		UserController.createAdmin(SystemUtils.getProperty("admin_username"), SystemUtils.getProperty("admin_password"));
    	staticFiles.location("/public");
    	port(Integer.parseInt(SystemUtils.getProperty("port", "4567")));
    	if (SystemUtils.inProduction() || SystemUtils.inTesting()){
    		
    	}
    	else{
    		enableDebugScreen();
    	}
    	
    }
    
    /**
     * Instantiates all the filters that happen before a request.
     */
    private static void beforeFilters(){
    	before("*",Filters.addTrailingSlashes);
    	before("*",Filters.ensureLoggedIn);
    }
    
    /**
     * Sets up all the GET routing for incoming requests.
     */
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
    	get(Path.Web.ONE_CONTACT,		ContactController.displayContact);
    	get(Path.Web.ADD_CONTACT,		ContactController.addContact);
    	get(Path.Web.EDIT_CONTACT,		ContactController.editContact);
    	get(Path.Web.BACKUP,			BackupUtils.backup);
    	get(Path.Web.RESTORE,			BackupUtils.restore);
    	get(Path.Web.ADMIN_USER_ADD,	AdminController.addUser);
    	get(Path.Web.SEARCH_RESULTS,	ContactController.searchContacts);
    	get(Path.Web.CHANGE_PASSWORD,	AdminController.changePassword);
    	get(Path.Web.ADD_SHARED_ADDRESS,AddressController.addSharedAddress);
    	get(Path.Web.HANDLE_SHARED_ADDRESS,AddressController.sharedAddressHandler);
    	get(Path.Web.ADD_LOCALE,		AdminController.addLocale);
    	get(Path.Web.GET_VCARD,			VCardController.downloadVCard);
    	//404 Routing
    	get("*",						ViewUtils.notFound);
    }
    /**
     * Sets up all the POST routing for incoming requests.
     */
    private static void postRouting(){
    	post(Path.Web.SEARCH_POST,		ContactController.contactSearchPost);
    	post(Path.Web.ADDRESS_POST,		AddressController.addressPost);
    	post(Path.Web.PHONE_NUMBER_POST,PhoneNumberController.phoneNumberPost);
    	post(Path.Web.LOGIN_POST,		LoginController.loginPost);
    	post(Path.Web.CONTACT_POST,		ContactController.contactPost);
    	post(Path.Web.ADMIN_POST,		AdminController.adminPost);
    	
    	//404 Routing
    	post("*",						ViewUtils.notFound);
    }
}
