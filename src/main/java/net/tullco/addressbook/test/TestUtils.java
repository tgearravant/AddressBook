package net.tullco.addressbook.test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import net.tullco.addressbook.App;
import net.tullco.addressbook.utils.BackupUtils;
import net.tullco.addressbook.utils.Path;
import net.tullco.addressbook.utils.SystemUtils;
import spark.utils.IOUtils;

public class TestUtils {
	public static String cookie="JSESSIONID=";
	public static void seedTestDB(){
		if(!SystemUtils.inTesting())
			return;
		InputStream input;
		input=App.class.getClassLoader().getResourceAsStream("test/seed_test_db.sql");
		if (input==null) {
			System.out.println("Could not load seed file...");
			return;
		}
		String dbSeedString;
		try{
			dbSeedString=IOUtils.toString(input);
		}catch(IOException e){
			return;
		}finally{
			try {
				input.close();
			} catch (IOException e) {}
		}
		String[] insertStrings=dbSeedString.split("\n");
		BackupUtils.executeInsertArray(insertStrings);
	}
	public static String getPage(String url){
		try{
			HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Cookie",TestUtils.cookie);
			con.connect();
			BufferedReader reader=new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
			String line;
			String page="";
			while((line=reader.readLine())!=null){
				page+=line;
			}
			reader.close();
			System.out.println(con.getHeaderFields());
			return page;
		}catch(Exception e){
			return "";
		}
	}
	public static void postToPage(String url, String postBody){
		try{
			System.out.println(url);
			HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setRequestProperty("Content-Type","multipart/form-data");
			con.setRequestProperty("Cookie",TestUtils.cookie);
			//con.setDoInput(false);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(postBody);
			System.out.println(con.getHeaderFields());
		}catch(Exception e){}
	}
	public static void login(String username,String password){
		String url = "http://127.0.0.1:4567"+Path.Web.LOGIN_POST;
		String postBody = "username="+username+"&password="+password;
		try{
			System.out.println(url);
			HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setRequestProperty("Content-Type","multipart/form-data");
			//con.setDoInput(false);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(postBody);
			System.out.println(con.getHeaderField("Set-Cookie"));
			TestUtils.cookie=con.getHeaderField("Set-Cookie").split(";")[0];
		}catch(Exception e){}
	}
}
