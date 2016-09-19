package net.tullco.addressbook.test_classes;

import static org.junit.Assert.*;

import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.tullco.addressbook.App;
import net.tullco.addressbook.test.TestUtils;
import net.tullco.addressbook.utils.Path;
import net.tullco.addressbook.utils.SQLUtils;
import net.tullco.addressbook.utils.SystemUtils;

public class APIAuthTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		SystemUtils.setTesting(true);
		SQLUtils.runMigrations();
		SQLUtils.truncateAllTables();
		TestUtils.seedTestDB();
		App.main(new String[0]);
	}

	@Test
	public void testAPIAuth() {
		String response = TestUtils.postToPage("http://127.0.0.1:4567"+Path.Web.API_AUTH, "username=fun&password=3");
		JSONObject json = new JSONObject(response);
		assertEquals("authentication", json.getString("api"));
		assertEquals("Invalid Credentials", json.getJSONObject("errors").getString("error_message"));
		response = TestUtils.postToPage("http://127.0.0.1:4567"+Path.Web.API_AUTH, "username=luke&password=3");
		json = new JSONObject(response);
		assertEquals("authentication", json.getString("api"));
		assertEquals("Invalid Credentials", json.getJSONObject("errors").getString("error_message"));
		response = TestUtils.postToPage("http://127.0.0.1:4567"+Path.Web.API_AUTH, "username=luke&password=Luke124");
		json = new JSONObject(response);
		assertEquals("authentication", json.getString("api"));
		assertEquals("luke", json.getJSONObject("user_info").getString("username"));
		assertEquals(100,json.getJSONObject("user_info").getString("api_key").length());
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		App.exit();
	}

}
