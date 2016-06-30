package net.tullco.addressbook.utils;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import spark.Request;
import spark.Response;
import spark.Route;


public class BackupUtils {

	private final static String BUCKET_NAME=SystemUtils.getProperty("s3_bucket","addressbook-backups");
	private final static String BACKUP_KEY=SystemUtils.getProperty("backup_key");
	
	private final static String OUTPUT_DESTINATION="%d%d%d%d%d_addressbook_backup";
	private final static String[] TABLES_TO_BACKUP= {"contacts","addresses","users","phone_numbers"};
	
	public static Route backup = (Request request, Response response) -> {
		String givenKey = request.queryParams("backup_key");
		if(givenKey==null || !givenKey.equals(BACKUP_KEY))
			return "Backup Unsuccessful. Bad Key.";
		s3Put(zipBackupFiles());
		return "Backup Successful...";
	};
	public static Route restore = (Request request, Response response) -> {
		String givenKey = request.queryParams("backup_key");
		if(givenKey==null || !givenKey.equals(BACKUP_KEY))
			return "Restore Unsuccessful. Bad backup_key.";
		String s3Key=request.queryParams("s3_key");
		if(s3Key==null)
			return "No S3 key provided in s3_key param";
		if(!s3HasKey(s3Key))
			return "Provided S3 key does not exist";
		restoreBackup(s3Key);
		return "Restore Successful...";
	};

	private static AmazonS3 s3Connect(){
		BasicAWSCredentials awsCreds = 
				new BasicAWSCredentials(SystemUtils.getProperty("s3_access_key_id")
						,SystemUtils.getProperty("s3_secret_key"));
		return new AmazonS3Client(awsCreds);
	}
	private static void s3Put(File f){
		AmazonS3 client = s3Connect();
		try{
			System.out.println("Backing up "+f.getName());
			client.putObject(BUCKET_NAME, getFileName()+".zip", f);
		}catch(AmazonServiceException e){
			handleAmazonServiceException(e);
		}catch(AmazonClientException e){
			handleAmazonClientException(e);
		}
	}
	private static File s3Get(String key){
		AmazonS3 client = s3Connect();
		
		S3Object backup = client.getObject(new GetObjectRequest(BUCKET_NAME,key));
		InputStream backupData= backup.getObjectContent();
		File temp=null;
		FileOutputStream out = null;
		byte[] buf = new byte[1024];
		int count=0;
		
		try{
			temp = File.createTempFile(key, ".tmp");
			temp.deleteOnExit();
			out = new FileOutputStream(temp);
			while( (count = backupData.read(buf)) != -1)
				out.write(buf, 0, count);
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try{
				backupData.close();
				backup.close();
				out.close();
			}catch(IOException e){}
		}
		return temp;
	}
	private static boolean s3HasKey(String key){
		AmazonS3 client = s3Connect();
		try{
			client.getObjectMetadata(BUCKET_NAME, key);
			return true;
		}catch(AmazonS3Exception e){
			return false;
		}
		
	}
	private static boolean restoreBackup(String key){
		String backup;
		try {
			backup = unzipBackupFile(s3Get(key));
			if (backup==null)
				return false;
		} catch (ZipException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		for(String s:TABLES_TO_BACKUP)
			SQLUtils.truncateTable(s);
		String[] stuff = backup.split("\n");
		for (String s:stuff){
			SQLUtils.executeInsert(s);
		}
		return true;
	}
	private static String unzipBackupFile(File f) throws ZipException, IOException{
		if(f==null)
			return null;
		System.out.println(f.getAbsolutePath());
		ZipFile zipFile=new ZipFile(f.getAbsolutePath());
		InputStream stream = null;
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while (entries.hasMoreElements()){
			ZipEntry entry=entries.nextElement();
			if(entry.getName().contains(".sql")){
				 stream = zipFile.getInputStream(entry);
				 break;
			}
		}
		if (stream==null){
			zipFile.close();
			return null;
		}
		String output = IOUtils.toString(stream);
		stream.close();
		zipFile.close();
		return output;
	}
	private static File zipBackupFiles(){
		System.out.println("Creating backup files...");
		String outputFileName = getFileName();
		File temp=null;
		try{
			temp = File.createTempFile(outputFileName, ".zip");
			temp.deleteOnExit();
			//File temp=new File("C:\\Users\\"+outputFileName+".zip");
			ZipOutputStream stream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(temp)));
			stream.setMethod(ZipOutputStream.DEFLATED);
			ZipEntry entry = new ZipEntry(outputFileName+".sql");
			stream.putNextEntry(entry);
			String backupString=createBackupString();
			stream.write(backupString.getBytes(), 0, backupString.length());
			stream.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{}
		return temp;
	}

	private static String createBackupString(){
		String backupString="";
		for(String table:TABLES_TO_BACKUP){
			try {
				backupString+=SQLUtils.getTableAsInsertString(table);
			} catch (SQLException e) {
				System.out.println("Error encountered backing up table: "+table);
			}
		}
		return backupString;
	}

	private static String getFileName(){
		Calendar cal = Calendar.getInstance();
		String outputFileName = String.format(OUTPUT_DESTINATION
				,cal.get(Calendar.YEAR)
				,cal.get(Calendar.MONTH)+1
				,cal.get(Calendar.DAY_OF_MONTH)
				,cal.get(Calendar.HOUR_OF_DAY)
				,cal.get(Calendar.MINUTE));
		return outputFileName;
	}
	private static void handleAmazonServiceException(AmazonServiceException ase){
        System.err.println("Caught an AmazonServiceException, which " +
        		"means your request made it " +
                "to Amazon S3, but was rejected with an error response" +
                " for some reason.");
        System.err.println("Error Message:    " + ase.getMessage());
        System.err.println("HTTP Status Code: " + ase.getStatusCode());
        System.err.println("AWS Error Code:   " + ase.getErrorCode());
        System.err.println("Error Type:       " + ase.getErrorType());
        System.err.println("Request ID:       " + ase.getRequestId());
	}
	private static void handleAmazonClientException(AmazonClientException ace){
		System.out.println("Caught an AmazonClientException, which " +
        		"means the client encountered " +
                "an internal error while trying to " +
                "communicate with S3, " +
                "such as not being able to access the network.");
        System.out.println("Error Message: " + ace.getMessage());
	}
}
