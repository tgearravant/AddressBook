package net.tullco.addressbook.test_classes;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.tullco.addressbook.address.Address;
import net.tullco.addressbook.test.TestUtils;
import net.tullco.addressbook.utils.SQLUtils;
import net.tullco.addressbook.utils.SystemUtils;

public class AddressTest {

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
	public void testSave() {
		Address a = Address.addressLoader(2,1);
	}
	
	@Test
	public void testDelete() {
		Address aLuke = Address.addressLoader(3, 2);
		assertNotEquals(null,aLuke);
		aLuke.delete();
		aLuke = Address.addressLoader(3, 2);
		assertEquals(null,Address.addressLoader(3, 2));
		Address aLeia = Address.addressLoader(3, 3);
		assertNotEquals(null,aLeia);
		aLeia.delete();
		aLeia = Address.addressLoader(3, 3);
		assertEquals(null,aLeia);
	}

	@Test
	public void testAddressLoader() {
		Address a = Address.addressLoader(1, 1);
		assertTrue(a.active());
		assertEquals(null,a.apartment());
		assertEquals("600 Awesome Ave",a.street());
		assertEquals("Tull",a.city());
		assertEquals("AR",a.state());
		assertEquals("57842",a.zipCode());
		assertEquals("United States",a.country());
		a = Address.addressLoader(2, 1);
		assertFalse(a.active());
		assertEquals("775",a.apartment());
		assertEquals("600 Nonsense Ave",a.street());
		assertEquals("Beverly",a.city());
		assertEquals("ME",a.state());
		assertEquals("17235",a.zipCode());
		assertEquals("Canada",a.country());
		a = Address.addressLoader(3, 2);
		assertFalse(a.active());
		a = Address.addressLoader(3, 3);
		assertTrue(a.active());
	}

	@Test
	public void testAddressesLoader() {
		List<Address> addresses = Address.addressesLoader(1);
		Address a = Address.getCurrentAddress(addresses);
		assertEquals("600 Awesome Ave",a.street());
		assertEquals("Tull",a.city());
		assertEquals("AR",a.state());
		assertEquals("57842",a.zipCode());
		assertEquals("United States",a.country());
		assertEquals(2,addresses.size());
	}

	@Test
	public void testAddAddressLink() {
		fail("Not yet implemented");
	}

}
