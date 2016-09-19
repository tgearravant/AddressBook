package net.tullco.addressbook.test_classes;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.tullco.addressbook.App;
import net.tullco.addressbook.address.Address;
import net.tullco.addressbook.contact.Contact;
import net.tullco.addressbook.phone_number.PhoneNumber;
import net.tullco.addressbook.test.TestUtils;
import net.tullco.addressbook.user.User;
import net.tullco.addressbook.utils.LocaleUtils;
import net.tullco.addressbook.utils.Path;
import net.tullco.addressbook.utils.SQLUtils;
import net.tullco.addressbook.utils.SystemUtils;

import static spark.Spark.stop;

import java.util.Calendar;
import java.util.List;

public class PostPageTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		SystemUtils.setTesting(true);
		SQLUtils.runMigrations();
		App.main(new String[0]);
	}
	@Before
	public void setUp() {
		TestUtils.getPage("http://127.0.0.1:4567"+Path.Web.LOGOUT);
		SQLUtils.truncateAllTables();
		TestUtils.seedTestDB();
		TestUtils.login("luke", "Luke124");
	}
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		stop();
	}

	@Test
	public void addAddressPost() {
		List<Address> addresses = Address.addressesLoader(3);
		assertEquals(1,addresses.size());
		assertNull(Address.addressLoader(4, 3));
		String postBody = "mode=add&street=fun&country=us&contact_id=3"
				+ "&active=1&apartment=3&zip_code=12345&city=pomp&state=circumstance";
		TestUtils.postToPage("http://127.0.0.1:4567"+Path.Web.ADDRESS_POST, postBody);
		addresses = Address.addressesLoader(3);
		assertEquals(2,addresses.size());
		Address a = Address.addressLoader(4, 3);
		assertNotNull(a);
		assertTrue(a.active());
		assertEquals("fun",a.street());
		assertEquals("us",a.getLocale());
		assertEquals(3,a.contactId());
		assertEquals("3",a.apartment());
		assertEquals("12345",a.zipCode());
		assertEquals("pomp",a.city());
		assertEquals("circumstance",a.state());
	}
	@Test
	public void editAddressPost() {
		assertNotNull(Address.addressLoader(3, 3));
		String postBody = "mode=edit&address_id=3&street=fun&country=us&contact_id=3"
				+ "&active=0&apartment=3&zip_code=12345&city=pomp&state=circumstance";
		TestUtils.postToPage("http://127.0.0.1:4567"+Path.Web.ADDRESS_POST, postBody);
		Address a = Address.addressLoader(3, 3);
		assertNotNull(a);
		assertFalse(a.active());
		assertEquals("fun",a.street());
		assertEquals("us",a.getLocale());
		assertEquals(3,a.contactId());
		assertEquals("3",a.apartment());
		assertEquals("12345",a.zipCode());
		assertEquals("pomp",a.city());
		assertEquals("circumstance",a.state());
	}
	@Test
	public void deleteAddressPost() {
		assertNotNull(Address.addressLoader(3,3));
		String postBody = "mode=delete&address_id=3&contact_id=3";
		TestUtils.postToPage("http://127.0.0.1:4567"+Path.Web.ADDRESS_POST, postBody);
		assertNull(Address.addressLoader(3,3));
	}
	@Test
	public void addContactPost() {
		assertNull(Contact.contactLoader(4));
		String postBody = "mode=add&first_name=Geralt&middle_name=of&last_name=Rivia"
				+ "&birthday=23&birthmonth=3&birthyear=1800&email=geralt@witchersrus.com&nickname=Wolf";
		TestUtils.postToPage("http://127.0.0.1:4567"+Path.Web.CONTACT_POST, postBody);
		Contact c=Contact.contactLoader(4);
		assertNotNull(c);
		assertEquals("Geralt",c.firstName());
		assertEquals("of",c.middleName());
		assertEquals("Rivia",c.lastName());
		assertEquals("geralt@witchersrus.com",c.email());
		assertEquals("Wolf",c.nickname());
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(1800, 2, 23);
		assertEquals(cal.getTime(),c.birthdate());
	}
	@Test
	public void editContactPost(){
		String postBody = "mode=edit&contact_id=2&first_name=Geralt&middle_name=of&last_name=Rivia"
				+ "&birthday=23&birthmonth=3&birthyear=1800&email=geralt@witchersrus.com&nickname=Wolf";
		TestUtils.postToPage("http://127.0.0.1:4567"+Path.Web.CONTACT_POST, postBody);
		Contact c = Contact.contactLoader(2);
		assertNotNull(c);
		assertEquals("Geralt",c.firstName());
		assertEquals("of",c.middleName());
		assertEquals("Rivia",c.lastName());
		assertEquals("geralt@witchersrus.com",c.email());
		assertEquals("Wolf",c.nickname());
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(1800, 2, 23);
		assertEquals(cal.getTime(),c.birthdate());
	}
	@Test
	public void deleteContactPost(){
		assertNotNull(Contact.contactLoader(2));
		String postBody = "mode=delete&contact_id=2";
		TestUtils.postToPage("http://127.0.0.1:4567"+Path.Web.CONTACT_POST, postBody);
		assertNull(Contact.contactLoader(2));
	}
	@Test
	public void addLocalePost(){
		assertEquals(2,LocaleUtils.allowedLocales().size());
		assertNotEquals("Netherlands",LocaleUtils.getLongLocaleName("nl"));
		String postBody = "mode=add_locale&long_name=Netherlands&locale=nl";
		TestUtils.postToPage("http://127.0.0.1:4567"+Path.Web.ADMIN_POST, postBody);
		assertEquals(3,LocaleUtils.allowedLocales().size());
		assertEquals("Netherlands",LocaleUtils.getLongLocaleName("nl"));
	}
	@Test
	public void editUserPost(){
		assertNull(User.userLoader("vader"));
		String postBody = "mode=edit_user&username=vader&password=darkside";
		TestUtils.postToPage("http://127.0.0.1:4567"+Path.Web.ADMIN_POST, postBody);
		User u = User.userLoader("vader");
		assertNotNull(u);
		assertTrue(u.checkPassword("darkside"));
		postBody = "mode=edit_user&username=vader&password=lightside";
		TestUtils.postToPage("http://127.0.0.1:4567"+Path.Web.ADMIN_POST, postBody);
		u = User.userLoader("vader");
		assertNotNull(u);
		assertTrue(u.checkPassword("lightside"));
	}
	@Test
	public void changePasswordPost(){
		User u = User.userLoader("luke");
		assertTrue(u.checkPassword("Luke124"));
		String postBody = "mode=change_password&password=darkside&confirm_password=darksid";
		TestUtils.postToPage("http://127.0.0.1:4567"+Path.Web.ADMIN_POST, postBody);
		u = User.userLoader("luke");
		assertTrue(u.checkPassword("Luke124"));
		postBody = "mode=change_password&password=darkside&confirm_password=darkside";
		TestUtils.postToPage("http://127.0.0.1:4567"+Path.Web.ADMIN_POST, postBody);
		u = User.userLoader("luke");
		assertFalse(u.checkPassword("Luke124"));
		assertTrue(u.checkPassword("darkside"));
	}
	@Test
	public void addPhoneNumberPost() {
		assertNull(PhoneNumber.phoneNumberLoader(4));
		String postBody = "mode=add&number=1234567890&type=mobile&locale=us&preferred=1&contact_id=3";
		TestUtils.postToPage("http://127.0.0.1:4567"+Path.Web.PHONE_NUMBER_POST, postBody);
		PhoneNumber pn = PhoneNumber.phoneNumberLoader(4);
		assertNotNull(pn);
		assertEquals("1234567890",pn.getNumber());
		assertEquals("mobile",pn.getType());
		assertEquals("us",pn.getLocale());
		assertEquals(3,pn.getContactId());
		assertTrue(pn.isPreferred());
	}
	@Test
	public void editPhoneNumberPost() {
		assertNotNull(PhoneNumber.phoneNumberLoader(3));
		String postBody = "mode=edit&number=1234567890&type=mobile&locale=us&preferred=1&contact_id=3&phone_number_id=3";
		TestUtils.postToPage("http://127.0.0.1:4567"+Path.Web.PHONE_NUMBER_POST, postBody);
		PhoneNumber pn = PhoneNumber.phoneNumberLoader(3);
		assertNotNull(pn);
		assertEquals("1234567890",pn.getNumber());
		assertEquals("mobile",pn.getType());
		assertEquals("us",pn.getLocale());
		assertEquals(3,pn.getContactId());
		assertTrue(pn.isPreferred());
	}
	@Test
	public void deletePhoneNumberPost() {
		assertNotNull(PhoneNumber.phoneNumberLoader(3));
		String postBody = "mode=delete&phone_number_id=3";
		TestUtils.postToPage("http://127.0.0.1:4567"+Path.Web.PHONE_NUMBER_POST, postBody);
		assertNull(PhoneNumber.phoneNumberLoader(3));
	}
}
