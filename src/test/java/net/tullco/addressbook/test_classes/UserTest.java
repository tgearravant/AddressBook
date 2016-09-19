package net.tullco.addressbook.test_classes;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.tullco.addressbook.test.TestUtils;
import net.tullco.addressbook.user.User;
import net.tullco.addressbook.utils.SQLUtils;
import net.tullco.addressbook.utils.SystemUtils;

public class UserTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		SystemUtils.setTesting(true);
		SQLUtils.runMigrations();
	}

	@Before
	public void setUp() throws Exception {
		SQLUtils.truncateAllTables();
		TestUtils.seedTestDB();
	}

	@Test
	public void testIsAdmin() {
		User u = User.userLoader("admin");
		assertTrue(u.isAdmin());
		u = User.userLoader("luke");
		assertFalse(u.isAdmin());
	}

	@Test
	public void testCheckPassword() {
		User u = User.userLoader("admin");
		assertTrue(u.checkPassword("password"));
		assertFalse(u.checkPassword("Password"));
		u = User.userLoader("luke");
		assertTrue(u.checkPassword("Luke124"));
		assertFalse(u.checkPassword("luke123"));
	}

	@Test
	public void testChangePassword() {
		User u = User.userLoader("luke");
		assertTrue(u.checkPassword("Luke124"));
		assertFalse(u.checkPassword("DarkSideRulez"));
		u.changePassword("DarkSideRulez");
		u = User.userLoader("luke");
		assertTrue(u.checkPassword("Luke124"));
		assertFalse(u.checkPassword("DarkSideRulez"));
		u.changePassword("DarkSideRulez");
		u.save();
		u = User.userLoader("luke");
		assertFalse(u.checkPassword("Luke124"));
		assertTrue(u.checkPassword("DarkSideRulez"));
	}

	@Test
	public void testUserLoader() {
		User u = User.userLoader("luke");
		assertEquals("luke",u.getUsername());
	}

	@Test
	public void testNewUser() {
		User u = User.userLoader("vader");
		assertEquals(null,u);
		u = User.newUser("vader", "NOOOOOOO!!!");
		assertEquals("vader",u.getUsername());
		assertTrue(u.checkPassword("NOOOOOOO!!!"));
		assertFalse(u.checkPassword("NOOOOO!!!"));
		u = User.userLoader("vader");
		assertEquals("vader",u.getUsername());
		assertTrue(u.checkPassword("NOOOOOOO!!!"));
		assertFalse(u.checkPassword("NOOOOO!!!"));
	}

	@Test
	public void testCreateAdminUser() {
		User u = User.userLoader("admin");
		assertTrue(u.checkPassword("password"));
		u.changePassword("oops");
		assertTrue(u.checkPassword("oops"));
		assertTrue(u.isAdmin());
		User.createAdminUser("palpatine", "GIVEINTOYOURANGER");
		u = User.userLoader("admin");
		assertEquals(null,u);
		u = User.userLoader("palpatine");
		assertTrue(u.checkPassword("GIVEINTOYOURANGER"));
		u.isAdmin();
	}
	
	@Test
	public void testAPIKey() {
		User u = User.userLoader("admin");
		assertNotNull(u);
		assertEquals("blah",u.getAPIKey());
		u = User.userLoaderAPI("blah");
		assertNotNull(u);
		assertEquals("blah",u.getAPIKey());
		assertEquals("admin",u.getUsername());
		u = User.userLoaderAPI("sdlkjf");
		assertNull(u);
		u = User.userLoader("luke");
		assertNotNull(u);
		String apiKey = u.getAPIKey();
		assertNotNull(apiKey);
		assertEquals(100,apiKey.length());
		u = User.userLoaderAPI(apiKey);
		assertNotNull(u);
		assertEquals("luke",u.getUsername());
		assertEquals(apiKey,u.getAPIKey());
	}
}
