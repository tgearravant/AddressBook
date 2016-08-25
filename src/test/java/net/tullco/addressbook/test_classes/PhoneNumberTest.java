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
		pn.setNumber("4447778888");
		pn.setLocale("ca");
		pn.setType("home");
		pn.setPreferred(true);
		pn.save();
		pn = PhoneNumber.phoneNumberLoader(1);
		assertEquals("4447778888",pn.getNumber());
		assertEquals("ca",pn.getLocale());
		assertEquals("home",pn.getType());
		assertTrue(pn.isPreferred());
	}

	@Test
	public void testDelete() {
		PhoneNumber pn = PhoneNumber.phoneNumberLoader(1);
		assertNotEquals(null,pn);
		pn.delete();
		pn = PhoneNumber.phoneNumberLoader(1);
		assertEquals(null,pn);
		
	}

	@Test
	public void testPhoneNumbersLoader() {
		List<PhoneNumber> numbers = PhoneNumber.phoneNumbersLoader(2);
		assertEquals(2, numbers.size());
		numbers = PhoneNumber.phoneNumbersLoader(3);
		assertEquals(1, numbers.size());
		numbers = PhoneNumber.phoneNumbersLoader(1);
		assertEquals(0, numbers.size());
	}

	@Test
	public void testPhoneNumberLoader() {
		PhoneNumber pn = PhoneNumber.phoneNumberLoader(1);
		assertEquals("8887774444",pn.getNumber());
		assertFalse(pn.isPreferred());
		pn = PhoneNumber.phoneNumberLoader(5555555);
		assertEquals(null,pn);
	}

	@Test
	public void testGetPreferredNumberOfType() {
		List<PhoneNumber>numbers=PhoneNumber.phoneNumbersLoader(2);
		PhoneNumber pn = PhoneNumber.phoneNumberLoader(2);
		assertEquals(pn, PhoneNumber.getPreferredNumberOfType(numbers, "mobile"));
	}

	@Test
	public void testGetPreferredNumbers() {
		List<PhoneNumber> numbers = PhoneNumber.phoneNumbersLoader(2);
		List<PhoneNumber> preferredNumbers = PhoneNumber.getPreferredNumbers(numbers);
		assertEquals(1, preferredNumbers.size());
		PhoneNumber pn = PhoneNumber.phoneNumberLoader(2);
		assertEquals(pn, preferredNumbers.get(0));
		preferredNumbers = PhoneNumber.getPreferredNumbers(PhoneNumber.phoneNumbersLoader(3));
		assertEquals(0, preferredNumbers.size());
	}

	@Test
	public void testGetNumbersOfType() {
		List<PhoneNumber> numbers = PhoneNumber.phoneNumbersLoader(2);
		List<PhoneNumber> mobileNumbers = PhoneNumber.getNumbersOfType(numbers, "mobile");
		assertEquals(2,mobileNumbers.size());
		mobileNumbers = PhoneNumber.getNumbersOfType(numbers, "work");
		assertEquals(0,mobileNumbers.size());
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
