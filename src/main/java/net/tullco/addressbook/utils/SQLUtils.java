package net.tullco.addressbook.utils;

import java.sql.*;

import org.apache.commons.lang.StringEscapeUtils;
import org.flywaydb.core.*;


public class SQLUtils {
	
	private final static String TABLE_SELECT_SQL = "SELECT * FROM %s";
	private final static String TABLE_DELETE_SQL="DELETE FROM %s";
	final private static String[] TABLE_LIST={"addresses","contact_addresses"
			,"contacts","login_attempt_history","phone_numbers","users","locales"};
	
	private static Connection conn=null;
	
	/**
	 * Execute the given string as an insert statement.
	 * Also prints the query to the console.
	 * @param statement The SQL query to be executed.
	 * @return The id of the inserted row.
	 */
	public static int executeInsert(String statement){
		System.out.println(statement);
		Connection c = getConnection();
		try{
			Statement s = c.createStatement();
			s.executeUpdate(statement);
			ResultSet rs=s.getGeneratedKeys();
			int last_insert_id = rs.getInt(1);
			s.close();
			return last_insert_id;
		} catch(SQLException e){
			e.printStackTrace();
			System.out.println("Some kind of SQL Problem...");
			return 0;
		}
	}
	
	/**
	 * Execute the given statement as a select statement.
	 * Please close the returned result set object when you're done with it. 
	 * @param statement The statement to be executed.
	 * @return A results set object with the results of the query.
	 */
	public static ResultSet executeSelect(String statement){
		System.out.println(statement);
		Connection c = getConnection();
		try{
			Statement s = c.createStatement();
			ResultSet rs = s.executeQuery(statement);
			return rs;
		}catch(SQLException e){
			System.err.println("SQL Problem of some description. Most likely a syntax error.");
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Execute the given statement as an update
	 * @param statement
	 * @return True if the statement was successful. False if there was an error.
	 */
	public static boolean executeUpdate(String statement){
		System.out.println(statement);
		Connection c= getConnection();
		try{
			Statement s = c.createStatement();
			s.executeUpdate(statement);
			s.close();
			return true;
		}catch(SQLException e){
			System.err.println("SQL Problem of some description. Most likely a syntax error.");
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Gets the connection to the database that is used by the execute methods.
	 * A single connection is shared by all methods. 
	 * @return The SQL connection object.
	 */
	private static Connection getConnection(){
		try {
			if (SQLUtils.conn==null || SQLUtils.conn.isClosed()){
				Connection c=null;
				try{
					Class.forName("org.sqlite.JDBC");
					c=DriverManager.getConnection("jdbc:sqlite:"+databaseLocation());
				}catch(SQLException e){
					System.err.println("Could not connect to database for some reason...");
					e.printStackTrace();
				}catch(ClassNotFoundException e){
					System.err.println("No JDBC Driver");
					e.printStackTrace();
				}
				SQLUtils.conn=c;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return SQLUtils.conn;
	}
	
	/**
	 * This works like String.format. Basically, give it a string and objects like you would
	 * normally with String.format, and it will escape all strings to be safe,
	 * surround strings in single quotes, and then perform a normal format with the results.
	 * 
	 * Try not to execute SQL statements without it.
	 * @param s The string to add the objects into.
	 * @param objs The objects to escape and add to the string.
	 * @return The escaped, safe string.
	 */
	public static String sqlSafeFormat(String s, Object... objs){
		for (int i=0; i < objs.length; i++){
			if (objs[i] instanceof String && objs[i] != null && !objs[i].equals("")){
				objs[i] = "'"+StringEscapeUtils.escapeSql((String)objs[i])+"'";
			}
		}
		return String.format(s,objs);
	}
	/**
	 * Converts the table into an insert string for backup/restore operations.
	 * @param table The table to convert into a series of insert strings.
	 * @return The table data as a series of insert strings. One per line, semicolon separated.
	 * @throws SQLException If there was a problem getting the insert string.
	 */
	public static String getTableAsInsertString(String table) throws SQLException{
		ResultSet rs = SQLUtils.executeSelect(String.format(TABLE_SELECT_SQL, table));
		ResultSetMetaData md =rs.getMetaData();
		
		String[] columnHeaders = new String[md.getColumnCount()];
		String[] columnTypes = new String[md.getColumnCount()];
		for(int i=1;i<=columnHeaders.length;i++){
			columnHeaders[i-1]=md.getColumnLabel(i);
			columnTypes[i-1]=md.getColumnTypeName(i);
		}
		String[] formats=new String[columnHeaders.length];
		for(int i=1;i<=columnHeaders.length;i++){
			if (columnTypes[i-1].equals("VARCHAR"))
				formats[i-1]="%s";
			else if (columnTypes[i-1].equals("INTEGER"))
				formats[i-1]="%d";
			else
				formats[i-1]="%s";
		}
		String insert="INSERT INTO "+table+" ("+String.join(",", columnHeaders)+") VALUES ("+String.join(",", formats)+");\n";
		String tableOutput="";
		while(rs.next()){
			Object[] values=new Object[columnHeaders.length];
			for(int i=1;i<=columnHeaders.length;i++){
				values[i-1]=rs.getObject(i);
			}
			tableOutput+=SQLUtils.sqlSafeFormat(insert, values);
		}
		rs.close();
		return tableOutput;
	}
	
	/**
	 * Primarily used in testing. It switches between the real DB and the test DB.
	 * @return The filename of the DB currently in use.
	 */
	public static String databaseLocation(){
		if(SystemUtils.inTesting())
			return "tests.db";
		else
			return "contacts.db";
	}

	/**
	 * Runs all the migrations in the resources/db/migration folder.
	 * @return True. May later implement a false if migrations fail.
	 */
	public static boolean runMigrations(){
		Flyway flyway = new Flyway();
		flyway.setDataSource("jdbc:sqlite:"+databaseLocation(),"sa",null);
		flyway.setLocations("classpath:db\\migration");
		System.out.println("Migrating...");
		/*for (String s:flyway.getLocations())
			System.out.println(s);
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		URL[] urls = ((URLClassLoader)cl).getURLs();
        for(URL url: urls){
        	System.out.println(url.getFile());
        }*/
		flyway.migrate();
		return true;
	}
	
	/**
	 * Removes all rows from the specified table.
	 * Currently only used by backup restoration methods.
	 * Be /very/ careful with it, as there are no other checks on it.
	 * @param table The table to truncate.
	 * @return True if the update was successful. False otherwise.
	 */
	protected static boolean truncateTable(String table){
		return executeUpdate(String.format(TABLE_DELETE_SQL, table));
	}
	public static void truncateAllTables(){
		for(String table:TABLE_LIST){
			truncateTable(table);
		}
	}
}
