package net.tullco.addressbook.utils;

import java.sql.*;
import org.flywaydb.core.*;

public class SQLiteUtils {
	private static Connection conn=null;
	public static void executeInsert(){
		getConnection();
	}
	public static void executeSelect(){
		
	}
	public static void executeUpdate(){
		
	}
	private static Connection getConnection(){
		if (SQLiteUtils.conn==null){
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
		return SQLiteUtils.conn;
	}
	
	public static boolean runMigrations(){
		Flyway flyway = new Flyway();
		flyway.setDataSource("jdbc:sqlite:contacts.db","sa",null);
		flyway.migrate();
		return true;
	}
}
