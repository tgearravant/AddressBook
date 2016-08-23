package net.tullco.addressbook.test_classes;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.tullco.addressbook.App;
import net.tullco.addressbook.address.Address;
import net.tullco.addressbook.test.TestUtils;
import net.tullco.addressbook.utils.Path;
import net.tullco.addressbook.utils.SQLUtils;
import net.tullco.addressbook.utils.SystemUtils;

import static spark.Spark.stop;

import java.util.List;

public class PostPageTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		SystemUtils.setTesting(true);
		App.main(new String[0]);
	}
	@Before
	public void setUp() {
		TestUtils.getPage("http://127.0.0.1:4567"+Path.Web.LOGOUT);
		SQLUtils.truncateAllTables();
		TestUtils.seedTestDB();
		TestUtils.login("luke", "Luke124");
	}
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		stop();
	}

	@Test
	public void addContactPost() {
		List<Address> addresses = Address.addressesLoader(3);
		assertEquals(1,addresses.size());
		assertEquals(null,Address.addressLoader(4, 3));
		String postBody = "mode=add&street=fun&country=us&contact_id=3&active=1&apartment=3&zip_code=12345";
		TestUtils.postToPage("http://127.0.0.1:4567"+Path.Web.ADDRESS_POST, postBody);
		addresses = Address.addressesLoader(3);
		assertEquals(2,addresses.size());
	}

}
