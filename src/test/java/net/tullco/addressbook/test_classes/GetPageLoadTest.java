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
	}
	@Before
	public void setUp() {
		TestUtils.getPage("http://127.0.0.1:4567"+Path.Web.LOGOUT);
		TestUtils.login("luke", "Luke124");
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
		assertTrue(page.contains("8887774444"));
		page = TestUtils.getPage("http://127.0.0.1:4567"+Path.Web.getContactPath(3));
		assertTrue(page.contains("Leia"));
		assertTrue(page.contains("Current Address"));
		assertTrue(!page.contains("Previous Address"));
	}
	@Test
	public void testSearch() {
		String page = TestUtils.getPage("http://127.0.0.1:4567"+Path.Web.getSearchResultsAddress("luke"));
		assertTrue(page.contains("Luke"));
		assertTrue(page.contains("Skywalker"));
		assertFalse(page.contains("Leia"));
		page = TestUtils.getPage("http://127.0.0.1:4567"+Path.Web.getSearchResultsAddress("leia"));
		assertTrue(page.contains("Skywalker"));
		assertTrue(page.contains("Leia"));
		assertFalse(page.contains("John"));
	}
	@Test
	public void testAddContact(){
		String page = TestUtils.getPage("http://127.0.0.1:4567"+Path.Web.ADD_CONTACT);
		assertTrue(page.contains("<input type=\"hidden\" value=\"add\" name=\"mode\" />"));
		assertFalse(page.contains("<input type=\"hidden\" value=\"delete\" name=\"mode\" />"));
	}
	@Test
	public void testEditContact(){
		String page = TestUtils.getPage("http://127.0.0.1:4567"+Path.Web.getContactEdit(2));
		assertTrue(page.contains("value=\"Luke\""));
		assertTrue(page.contains("<input type=\"hidden\" value=\"edit\" name=\"mode\" />"));
		assertTrue(page.contains("<input type=\"hidden\" value=\"delete\" name=\"mode\" />"));
		assertTrue(page.contains("<option value=\"12\"  selected=\"selected\" >"));
		assertTrue(page.contains("<option value=\"1982\"  selected=\"selected\" >"));
	}
	@Test
	public void testAddAddress(){
		String page = TestUtils.getPage("http://127.0.0.1:4567"+Path.Web.getAddAddress(2));
		assertTrue(page.contains("<input type=\"checkbox\" value=\"1\" id=\"active\" name=\"active\" checked=\"checked\" />"));
		assertTrue(page.contains("<input type=\"hidden\" value=\"add\" name=\"mode\" />"));
		assertFalse(page.contains("<input type=\"hidden\" value=\"delete\" name=\"mode\" />"));
	}
	@Test
	public void testEditAddress(){
		String page = TestUtils.getPage("http://127.0.0.1:4567"+Path.Web.getAddressEdit(2, 3));
		assertTrue(page.contains("<input type=\"checkbox\" value=\"1\" id=\"active\" name=\"active\" />"));
		assertFalse(page.contains("<input type=\"hidden\" value=\"add\" name=\"mode\" />"));
		assertTrue(page.contains("<input type=\"hidden\" value=\"edit\" name=\"mode\" />"));
		assertTrue(page.contains("<input type=\"hidden\" value=\"delete\" name=\"mode\" />"));
	}
	@Test
	public void testAddPhoneNumber(){
		String page = TestUtils.getPage("http://127.0.0.1:4567"+Path.Web.getAddPhoneNumber(2));
		assertTrue(page.contains("<input type=\"checkbox\" value=\"1\" id=\"preferred\" name=\"preferred\" checked=\"checked\" />"));
		assertTrue(page.contains("<input type=\"hidden\" value=\"add\" name=\"mode\" />"));
		assertFalse(page.contains("<input type=\"hidden\" value=\"delete\" name=\"mode\" />"));
	}
	@Test
	public void testEditPhoneNumber(){
		String page = TestUtils.getPage("http://127.0.0.1:4567"+Path.Web.getEditPhoneNumber(1));
		assertFalse(page.contains("<input type=\"hidden\" value=\"add\" name=\"mode\" />"));
		assertTrue(page.contains("<input type=\"hidden\" value=\"edit\" name=\"mode\" />"));
		assertTrue(page.contains("<input type=\"hidden\" value=\"delete\" name=\"mode\" />"));
		assertTrue(page.contains("<input type=\"checkbox\" value=\"1\" id=\"preferred\" name=\"preferred\" />"));
	}
	@Test
	public void testAddSharedAddress(){
		String page = TestUtils.getPage("http://127.0.0.1:4567"+Path.Web.getAddSharedAddress(2));
		assertTrue(page.contains("Share Address"));
	}
	@Test
	public void testSharedAddressSearch(){
		String page = TestUtils.getPage("http://127.0.0.1:4567"+Path.Web.getSearchResultsAddress("john"));
		assertTrue(page.contains("John Doe"));
		assertFalse(page.contains("Leia"));
	}
	@Test
	public void testStylesheet(){
		String page = TestUtils.getPage("http://127.0.0.1:4567/css/style.css");
		assertTrue(page.contains("checkbox"));
	}
	@Test
	public void test404(){
		String page = TestUtils.getPage("http://127.0.0.1:4567/beverlyisthegreatest");
		System.out.println(page);
		assertEquals("",page);
	}
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		stop();
	}
}
