package net.tullco.addressbook.test;

import java.io.IOException;
import java.io.InputStream;

import net.tullco.addressbook.App;
import net.tullco.addressbook.utils.BackupUtils;
import net.tullco.addressbook.utils.SystemUtils;
import spark.utils.IOUtils;

public class TestUtils {
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
}
