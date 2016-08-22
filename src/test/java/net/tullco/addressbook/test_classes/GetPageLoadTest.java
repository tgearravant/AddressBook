package net.tullco.addressbook.test_classes;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.tullco.addressbook.test.TestUtils;
import net.tullco.addressbook.utils.Path;
import net.tullco.addressbook.utils.SQLUtils;
import net.tullco.addressbook.utils.SystemUtils;
import net.tullco.addressbook.App;

import static spark.Spark.stop;

public class GetPageLoadTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		SystemUtils.setTesting(true);
		SQLUtils.trucateAllTables();
		App.main(new String[0]);
		TestUtils.seedTestDB();
		TestUtils.login("luke", "Luke124");
	}
	@Before
	public void setUp() {
	}
	@Test
	public void testIndex() {
		String page = TestUtils.getPage("http://127.0.0.1:4567/");
		assertTrue(page.contains("Luke"));
		assertTrue(page.contains("Leia"));
		assertTrue(page.contains("Rebel"));
	}
	@Test
	public void testDetails() {
		String page = TestUtils.getPage("http://127.0.0.1:4567"+Path.Web.getContactPath(2));
		assertTrue(page.contains("Luke"));
		assertTrue(page.contains("Skywalker"));
		assertTrue(!page.contains("Current Address"));
		assertTrue(page.contains("Previous Address"));
		assertTrue(page.contains("Rebel Base"));
		page = TestUtils.getPage("http://127.0.0.1:4567"+Path.Web.getContactPath(3));
		assertTrue(page.contains("Leia"));
		assertTrue(page.contains("Current Address"));
		assertTrue(!page.contains("Previous Address"));
	}
	@Test
	public void testSearch() {
		String page = TestUtils.getPage("http://127.0.0.1:4567"+Path.Web.getSearchResultsAddress("luke"));
		System.out.println(page);
		System.out.println("http://127.0.0.1:4567"+Path.Web.getSearchResultsAddress("luke"));
		assertTrue(page.contains("Luke"));
		assertTrue(page.contains("Skywalker"));
		assertFalse(page.contains("Leia"));
		page = TestUtils.getPage("http://127.0.0.1:4567"+Path.Web.getSearchResultsAddress("leia"));
		assertTrue(page.contains("Skywalker"));
		assertTrue(page.contains("Leia"));
		assertFalse(page.contains("John"));
	}
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		stop();
	}
}
