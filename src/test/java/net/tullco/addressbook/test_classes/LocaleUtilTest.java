package net.tullco.addressbook.test_classes;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.tullco.addressbook.test.TestUtils;
import net.tullco.addressbook.utils.LocaleUtils;
import net.tullco.addressbook.utils.SQLUtils;
import net.tullco.addressbook.utils.SystemUtils;

public class LocaleUtilTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		SystemUtils.setTesting(true);
		SQLUtils.runMigrations();
	}

	@Before
	public void setUp() throws Exception {
		SQLUtils.truncateAllTables();
		TestUtils.seedTestDB();
		LocaleUtils.expireLocaleCache();
	}

	@Test
	public void testLocales() {
		Map<String,String> locales = LocaleUtils.allowedLocales();
		assertEquals("United States",locales.get("us"));
		assertEquals("Canada",locales.get("ca"));
		assertEquals(null,locales.get("nl"));
		SQLUtils.executeInsert("INSERT INTO locales (locale, long_name) VALUES ('nl','Netherlands')");
		locales = LocaleUtils.allowedLocales();
		assertEquals(null,locales.get("nl"));
		LocaleUtils.expireLocaleCache();
		locales = LocaleUtils.allowedLocales();
		assertEquals("Netherlands",locales.get("nl"));
		LocaleUtils.addLocale("gb", "United Kingdom");
		locales = LocaleUtils.allowedLocales();
		assertEquals("United Kingdom",locales.get("gb"));
	}

	@Test
	public void testAllowedLocalesList() {
		List<String[]> localeList = LocaleUtils.allowedLocalesList();
		assertEquals("us",localeList.get(0)[0]);
		assertEquals("United States",localeList.get(0)[1]);
		assertEquals("ca",localeList.get(1)[0]);
		assertEquals("Canada",localeList.get(1)[1]);
	}

	@Test
	public void testGetLongLocaleName() {
		assertEquals("United States",LocaleUtils.getLongLocaleName("us"));
		assertEquals("Canada",LocaleUtils.getLongLocaleName("ca"));
		assertEquals("Invalid Locale",LocaleUtils.getLongLocaleName("gb"));
	}

}
