package net.tullco.addressbook.application;

import static spark.Spark.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        staticFiles.location("\\public");
        get("/hello",(req,res) -> "Hello World");
    }
}
