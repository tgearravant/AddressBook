package net.tullco.addressbook.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;


public class BackupUtils {
	private final String OUTPUT_DESTINATION="%d%d%d%d%d_addressbook_backup";
	private final String[] tablesToBackup= {"contacts","addresses","users","phone_numbers"};
	public void s3Put(){
		
	}
	public void s3Get(){
		
	}
	public File zipBackupFiles(String backup) throws IOException{
		Calendar cal = Calendar.getInstance();
		String outputFileName = String.format(OUTPUT_DESTINATION
				,cal.get(Calendar.YEAR)
				,cal.get(Calendar.MONTH)+1
				,cal.get(Calendar.DAY_OF_MONTH));
		File temp = File.createTempFile(outputFileName, ".zip"); 
		ZipOutputStream stream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(temp)));
		stream.setMethod(ZipOutputStream.DEFLATED);
		ZipEntry entry = new ZipEntry(outputFileName+".sql");
		stream.putNextEntry(entry);
		stream.write("lol".getBytes(), 0, "lol".length());
		stream.close();
		return temp;
	}
	public void createBackupString(){
		
	}
	public void getTable(String table){
		
	}
	public void s3Connect(){
		BasicAWSCredentials awsCreds = new BasicAWSCredentials("", "");
		AmazonS3 client = new AmazonS3Client(awsCreds);
	}
}
