package net.tullco.addressbook.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ListObjectsV2Request;


import spark.Request;
import spark.Response;
import spark.Route;


public class BackupUtils {

	private final static String BUCKET_NAME=SystemUtils.getProperty("s3_bucket","addressbook-backups");
	
	private final static String OUTPUT_DESTINATION="%d%d%d%d%d_addressbook_backup";
	private final static String[] TABLES_TO_BACKUP= {"contacts","addresses","users","phone_numbers"};
	
	public static void s3Put(File f){
		AmazonS3 client = s3Connect();
		try{
			System.out.println("Backing up "+f.getName());
			client.putObject(BUCKET_NAME, f.getName(), f);
		}catch(AmazonServiceException e){
			handleAmazonServiceException(e);
		}catch(AmazonClientException e){
			handleAmazonClientException(e);
		}
	}
	public void s3Get(){
		
	}
	public String[] s3List(){
		final ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(BUCKET_NAME).withMaxKeys(50);
		req.hashCode();
		return new String[0];
	}
	public static Route backup = (Request request, Response response) -> {
		return "Backup Successful...";
	};
	public static File zipBackupFiles(){
		Calendar cal = Calendar.getInstance();
		String outputFileName = String.format(OUTPUT_DESTINATION
				,cal.get(Calendar.YEAR)
				,cal.get(Calendar.MONTH)+1
				,cal.get(Calendar.DAY_OF_MONTH)
				,cal.get(Calendar.HOUR_OF_DAY)
				,cal.get(Calendar.MINUTE));
		File temp=null;
		try{
			temp = File.createTempFile(outputFileName, ".zip");
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
			// TODO Auto-generated catch block
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

	private static AmazonS3 s3Connect(){
		BasicAWSCredentials awsCreds = 
				new BasicAWSCredentials(SystemUtils.getProperty("s3_access_key_id")
						,SystemUtils.getProperty("s3_secret_key"));
		return new AmazonS3Client(awsCreds);
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
