package net.tullco.addressbook.test_classes;

import static org.junit.Assert.*;

import java.sql.ResultSet;
import java.sql.SQLException;
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
		SQLUtils.truncateAllTables();
		TestUtils.seedTestDB();
	}

	@Test
	public void testSave() {
		Address a = Address.addressLoader(1,1);
		assertTrue(a.active());
		a = Address.addressLoader(2,1);
		a.setStreet("Boardwalk");
		a.setCity("New York");
		a.setActive(true);
		a.save();
		a = Address.addressLoader(2,1);
		assertTrue(a.active());
		assertEquals("Boardwalk",a.street());
		assertEquals("New York",a.city());
		a = Address.addressLoader(1,1);
		assertFalse(a.active());
	}
	
	@Test
	public void testDelete() {
		Address aLuke = Address.addressLoader(3, 2);
		assertNotEquals(null,aLuke);
		aLuke.delete();
		aLuke = Address.addressLoader(3, 2);
		assertEquals(null,aLuke);
		ResultSet rs = SQLUtils.executeSelect("SELECT count(*) AS count_addresses FROM addresses WHERE id=3");
		try{
			while(rs.next()){
				int count=rs.getInt("count_addresses");
				assertEquals(1,count);
			}
		}catch(SQLException e){
			fail(e.getMessage());
		}
		Address aLeia = Address.addressLoader(3, 3);
		assertNotEquals(null,aLeia);
		aLeia.delete();
		aLeia = Address.addressLoader(3, 3);
		assertEquals(null,aLeia);
		rs = SQLUtils.executeSelect("SELECT count(*) AS count_addresses FROM addresses WHERE id=3");
		try{
			while(rs.next()){
				int count=rs.getInt("count_addresses");
				assertEquals(0,count);
			}
		}catch(SQLException e){
			fail(e.getMessage());
		}
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
		a = Address.addressLoader(1, 3);
		assertEquals(null,a);
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
	public void testAddressSearch() {
		List<Address> addresses = Address.addressesLoaderByName("Skywalker");
		assertEquals(2,addresses.size());
		assertEquals(3,addresses.get(0).id());
		assertEquals(3,addresses.get(1).id());
	}
	@Test
	public void testGoogleMapsLink() {
		Address a = Address.addressLoader(1, 1);
		String mapsLink = a.getGoogleMapsLink();
		assertTrue(mapsLink.contains(a.street().replace(' ', '+')));
		assertTrue(mapsLink.contains(a.city().replace(' ', '+')));
		assertTrue(mapsLink.contains(a.state().replace(' ', '+')));
		assertTrue(mapsLink.contains(a.zipCode().replace(' ', '+')));
		assertTrue(mapsLink.contains(a.country().replace(' ', '+')));
	}
}
