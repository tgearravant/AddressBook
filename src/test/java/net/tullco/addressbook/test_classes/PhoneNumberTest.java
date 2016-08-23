package net.tullco.addressbook.test_classes;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.tullco.addressbook.phone_number.PhoneNumber;
import net.tullco.addressbook.test.TestUtils;
import net.tullco.addressbook.utils.SQLUtils;
import net.tullco.addressbook.utils.SystemUtils;

public class PhoneNumberTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		SystemUtils.setTesting(true);
	}

	@Before
	public void setUp() throws Exception {
		SQLUtils.truncateAllTables();
		TestUtils.seedTestDB();
	}

	@Test
	public void testSave() {
		PhoneNumber pn = PhoneNumber.phoneNumberLoader(1);
	}

	@Test
	public void testDelete() {
		fail("Not yet implemented");
	}

	@Test
	public void testPhoneNumbersLoader() {
		fail("Not yet implemented");
	}

	@Test
	public void testPhoneNumberLoader() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsPreferred() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetPreferred() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPreferredNumberOfType() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPreferredNumbers() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetNumbersOfType() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAllowedTypes() {
		String[] types = PhoneNumber.getAllowedTypes();
		assertEquals(4,types.length);
		for(String s : types){
			System.out.println(s);
			assertTrue(s.equals("mobile") || s.equals("home") || s.equals("work") || s.equals("other"));
		}
	}
}
