package net.tullco.addressbook.test_classes;

import static org.junit.Assert.*;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.tullco.addressbook.test.TestUtils;
import net.tullco.addressbook.utils.SQLUtils;
import net.tullco.addressbook.utils.SystemUtils;

public class SQLUtilTest {

	@BeforeClass
	public static void setUpClass() throws Exception {
		SystemUtils.setTesting(true);
		SQLUtils.runMigrations();
		SQLUtils.trucateAllTables();
		TestUtils.seedTestDB();
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testExecuteInsert() {
		int id = SQLUtils.executeInsert("INSERT INTO login_attempt_history (username,ip_address,user_agent,created_at,succeeded) VALUES ('luke','127.0.0.1','rebel_computing_v1.6.2',current_timestamp,1)");
		assertEquals(1,id);
		id = SQLUtils.executeInsert("INSERT INTO login_attempt_history (username,ip_address,user_agent,created_at,succeeded) VALUES ('luke','127.0.0.1','rebel_computing_v1.6.2',current_timestamp,1)");
		assertEquals(2,id);
	}

	@Test
	public void testExecuteSelect() {
		ResultSet rs = SQLUtils.executeSelect("SELECT count(*) AS contacts_count FROM contacts");
		int rows=0;
		try{
			while(rs.next()){
				int count=rs.getInt("contacts_count");
				assertEquals(3,count);
				rows++;
			}
		}catch(SQLException e){
			fail(e.getMessage());
		}
		assertEquals(1,rows);
	}

	@Test
	public void testExecuteUpdate() {
		assertTrue(SQLUtils.executeUpdate("DELETE FROM contacts WHERE id=3"));
		ResultSet rs = SQLUtils.executeSelect("SELECT count(*) AS contacts_count FROM contacts");
		try{
			while(rs.next()){
				int count=rs.getInt("contacts_count");
				assertEquals(2,count);
			}
		}catch(SQLException e){
			fail(e.getMessage());
		}
	}
 
	@Test
	public void testSqlSafeFormat() {
		String template="%s, %d, %s";
		String statement=SQLUtils.sqlSafeFormat(template, "test",3,null);
		assertEquals("'test', 3, null",statement);
		template="%s, %d, %d";
		statement=SQLUtils.sqlSafeFormat(template, "test",3,null);
		assertEquals("'test', 3, null",statement);
	}

	@Test
	public void testGetTableAsInsertString() {
		try{
			String output = SQLUtils.getTableAsInsertString("contacts");
			String[] inserts=output.split("\n");
			int i = 0;
			for (String s: inserts)
				if (s.equals("INSERT INTO contacts (id,first_name,middle_name,last_name,birthdate,email) VALUES (2,'Luke',null,'Skywalker',408542315874,'luke@lightside.force');"))
					i++;
			assertEquals(1,i);
		}catch(SQLException e){
			fail("SQL Exception: "+e.getMessage());
		}
	}

	@Test
	public void testDatabaseLocation() {
		assertEquals("tests.db",SQLUtils.databaseLocation());
	}

}
