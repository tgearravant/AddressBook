package net.tullco.addressbook.test_classes;

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

import net.tullco.addressbook.utils.SystemUtils;

public class SystemUtilTest {

	@BeforeClass
	public static void setUpBeforeClass() {
		SystemUtils.setTesting(true);
	}
	
	@Test
	public void testInProduction() {
		assertFalse(SystemUtils.inProduction());
	}

	@Test
	public void testInTesting() {
		SystemUtils.setTesting(false);
		assertFalse(SystemUtils.inTesting());
		SystemUtils.setTesting(true);
		assertTrue(SystemUtils.inTesting());
	}

	
	@Test
	public void testGetPropertyStringString() {
		assertEquals("habi",SystemUtils.getProperty("oops", "habi"));
	}

	@Test
	public void testGetPropertyString() {
		assertEquals("admin",SystemUtils.getProperty("admin_username"));
	}

	@Test
	public void testCheckForRequiredProperties() {
		try{
			SystemUtils.checkForRequiredProperties();
		}catch(RuntimeException e){
			fail("A required property in the default file is missing. I suggest you fix it. ;)");
		}
	}

}
