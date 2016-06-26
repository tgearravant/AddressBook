package net.tullco.addressbook.utils;

import java.sql.*;

import org.apache.commons.lang.StringEscapeUtils;
import org.flywaydb.core.*;

public class SQLUtils {
	private static Connection conn=null;
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
	private static Connection getConnection(){
		try {
			if (SQLUtils.conn==null || SQLUtils.conn.isClosed()){
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
				SQLUtils.conn=c;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return SQLUtils.conn;
	}
	public static String sqlSafeFormat(String s, Object... objs){
		for (int i=0; i < objs.length; i++){
			if (objs[i] instanceof String && objs[i] != null){
				objs[i] = "'"+StringEscapeUtils.escapeSql((String)objs[i])+"'";
			}
		}
		return String.format(s,objs);
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
