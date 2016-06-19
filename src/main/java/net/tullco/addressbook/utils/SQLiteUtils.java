package net.tullco.addressbook.utils;

import java.sql.*;
import org.flywaydb.core.*;

public class SQLiteUtils {
	private static Connection conn=null;
	public static void executeInsert(){
		getConnection();
	}
	public static ResultSet executeSelect(String statement){
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
	public static void executeUpdate(){
		
	}
	private static Connection getConnection(){
		try {
			if (SQLiteUtils.conn==null || SQLiteUtils.conn.isClosed()){
				Connection c=null;
				try{
					Class.forName("org.sqlite.JDBC");
					c=DriverManager.getConnection("jdbc:sqlite:contacts.db");
				}catch(SQLException e){
					System.err.println("Could not connect to database for some reason...");
					e.printStackTrace();
				}catch(ClassNotFoundException e){
					System.err.println("No JDBC Driver");
					e.printStackTrace();
				}
				SQLiteUtils.conn=c;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return SQLiteUtils.conn;
	}
	
	public static boolean runMigrations(){
		Flyway flyway = new Flyway();
		flyway.setDataSource("jdbc:sqlite:contacts.db","sa",null);
		flyway.setLocations("classpath:db\\migration");
		flyway.getLocations();
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
}
