package net.tullco.addressbook.test_classes;

import static org.junit.Assert.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.tullco.addressbook.App;
import net.tullco.addressbook.test.TestUtils;
import net.tullco.addressbook.utils.Path;
import net.tullco.addressbook.utils.SQLUtils;
import net.tullco.addressbook.utils.SystemUtils;

public class APIDataSyncTest {

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
		String response = TestUtils.postToPage("http://127.0.0.1:4567"+Path.Web.API_DATA_SYNC, "api_key=fun");
		JSONObject json = new JSONObject(response);
		assertEquals("data_sync",json.getString("api"));
		assertEquals("Invalid Key",json.getJSONObject("errors").getString("error_message"));
		response = TestUtils.postToPage("http://127.0.0.1:4567"+Path.Web.API_DATA_SYNC, "oops=oops");
		json = new JSONObject(response);
		assertEquals("data_sync",json.getString("api"));
		assertEquals("No Key",json.getJSONObject("errors").getString("error_message"));
		response = TestUtils.postToPage("http://127.0.0.1:4567"+Path.Web.API_DATA_SYNC, "api_key=blah");
		json = new JSONObject(response);
		System.out.println(response);
		boolean foundLuke=false;
		JSONArray contactArray = json.getJSONArray("contacts");
		assertEquals(3,contactArray.length());
		for(Object contactObject: contactArray){
			JSONObject contactJson = (JSONObject) contactObject;
			if(contactJson.getString("first_name").equals("Luke")){
				foundLuke=true;
				assertEquals("Skywalker",contactJson.getString("last_name"));
				assertEquals("luke@lightside.force",contactJson.getString("email"));
				assertEquals("Lu",contactJson.getString("nickname"));
				assertEquals(2,contactJson.getInt("id"));
				assertEquals("12/12/1982",contactJson.getString("birthdate"));
				JSONArray addresses = contactJson.getJSONArray("addresses");
				assertEquals(1,addresses.length());
				JSONObject address = addresses.getJSONObject(0);
				assertEquals("911 Rebel Base",address.getString("street"));
				assertEquals("Big Glacier",address.getString("city"));
				assertFalse(address.getBoolean("active"));
				assertEquals(3,address.getInt("id"));
				assertEquals("Hoth",address.getString("state"));
				assertEquals("Canada",address.getString("country"));
				JSONArray phoneNumbers = contactJson.getJSONArray("phone_numbers");
				assertEquals(2,phoneNumbers.length());
				boolean foundNumber = false;
				for(Object o: phoneNumbers){
					JSONObject pnJson = (JSONObject) o;
					if(pnJson.getString("number").equals("8887774444")){
						foundNumber=true;
						assertEquals("United States",pnJson.getString("country"));
						assertEquals(1,pnJson.getInt("id"));
						assertEquals("mobile",pnJson.getString("type"));
						assertFalse(pnJson.getBoolean("preferred"));
					}
				}
				assertTrue(foundNumber);
			}
		}
		assertTrue(foundLuke);
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		App.exit();
	}

}
