package net.tullco.addressbook.test_classes;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.tullco.addressbook.test.TestUtils;
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
	@Test
	public void test() {
		String page = TestUtils.getPage("http://127.0.0.1:4567/");
		System.out.println(TestUtils.cookie);
		System.out.println(page);
		assertTrue(page.contains("Luke"));
	}
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		stop();
	}
}
