package net.tullco.addressbook.test;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.tullco.addressbook.address.Address;
import net.tullco.addressbook.contact.Contact;
import net.tullco.addressbook.utils.DisplayUtils;
import net.tullco.addressbook.utils.SQLUtils;
import net.tullco.addressbook.utils.SystemUtils;

public class ContactTest {

	@BeforeClass
	public static void setUpClass() throws Exception {
		SystemUtils.setTesting(true);
		SQLUtils.runMigrations();
	}

	@Before
	public void setUp() throws Exception {
		SQLUtils.trucateAllTables();
		TestUtils.seedTestDB();
	}

	@Test
	public void testContactLoader() {
		Contact c = Contact.contactLoader(1);
		assertEquals("John",c.firstName());
		assertEquals("Lost",c.middleName());
		assertEquals("Doe",c.lastName());
		Calendar cal = Calendar.getInstance();
		cal.set(1986, 1, 11, 0, 0, 0);
		assertEquals(
				DisplayUtils.dateToString(cal.getTime())
				,DisplayUtils.dateToString(c.birthdate())
				);
		assertEquals("jdoe@gmail.com",c.email());
	}
	@Test
	public void testContactEdit() {
		Contact c = Contact.contactLoader(1);
		c.setFirstName("Jane");
		c.setMiddleName("Found");
		c.setLastName("Doette");
		Calendar cal = Calendar.getInstance();
		cal.set(1984, 7, 15, 0, 0, 0);
		c.setBirthdate(cal.getTime());
		c.setEmail("jdoette@gmail.com");
		c.save();
		c=Contact.contactLoader(1);
		assertEquals("Jane",c.firstName());
		assertEquals("Found",c.middleName());
		assertEquals("Doette",c.lastName());
		assertEquals(
				DisplayUtils.dateToString(cal.getTime())
				,DisplayUtils.dateToString(c.birthdate())
				);
		assertEquals("jdoette@gmail.com",c.email());
	}
	@Test
	public void testContactAdd() {
		HashMap<String,String> values = new HashMap<String,String>();
		values.put("first_name", "Jane");
		values.put("middle_name", "Found");
		values.put("last_name", "Doette");
		Calendar cal = Calendar.getInstance();
		cal.set(1984, 7, 15, 0, 0, 0);
		values.put("birthdate",new Long(cal.getTimeInMillis()).toString());
		values.put("email", "jdoette@gmail.com");
		Contact c = new Contact(values);
		c.save();
		int id=c.getId();
		c=Contact.contactLoader(id);
		assertEquals("Jane",c.firstName());
		assertEquals("Found",c.middleName());
		assertEquals("Doette",c.lastName());
		assertEquals(
				DisplayUtils.dateToString(cal.getTime())
				,DisplayUtils.dateToString(c.birthdate())
				);
		assertEquals("jdoette@gmail.com",c.email());
	}
	@Test
	public void testContactAddressLoading() {
		Contact c = Contact.contactLoader(1);
		List<Address> addresses = c.addresses();
		assertTrue(addresses.size()>=2);
		Address currentAddress = c.currentAddress();
		assertTrue(currentAddress.active());
		assertEquals(null,currentAddress.apartment());
		assertEquals("600 Awesome Ave",currentAddress.street());
		assertEquals("Tull",currentAddress.city());
		assertEquals("AR",currentAddress.state());
		assertEquals("57842",currentAddress.zipCode());
		assertEquals("United States",currentAddress.country());
		addresses.remove(currentAddress);
		assertTrue(addresses.size()>=1);
		Address otherAddress=addresses.get(0);assertTrue(currentAddress.active());
		assertEquals("775",otherAddress.apartment());
		assertEquals("600 Nonsense Ave",otherAddress.street());
		assertEquals("Beverly",otherAddress.city());
		assertEquals("ME",otherAddress.state());
		assertEquals("17235",otherAddress.zipCode());
		assertEquals("Canada",otherAddress.country());
	}
}
