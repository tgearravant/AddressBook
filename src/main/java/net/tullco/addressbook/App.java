package net.tullco.addressbook;

import static spark.Spark.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	initialConfiguration();
    	getRouting();
        System.out.println(System.getProperty("os.name"));
        get("/hello",(req,res) -> "Hello World");
    }
    private static void initialConfiguration(){
    	staticFiles.location("\\public");
    	port(4567);
    	
    }
    private static void getRouting(){
    	//Test Page
    	get("/hello",(req,res) -> "Hello World");
    	//Index Routing
    	get("/",(req,res) -> "Hello World");
    }
    private static void before(){
    }
}
