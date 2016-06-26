package net.tullco.addressbook.login;

import java.util.Date;

import net.tullco.addressbook.utils.SQLUtils;
import net.tullco.addressbook.utils.ViewUtils;
import spark.Request;

public class LoginHistory {
	
	public final static String ADD_HISTORY_SQL = "INSERT INTO login_attempt_history "
			+ "(username,ip_address,user_agent,created_at,succeeded) "
			+ "VALUES (%s,%s,%s,%d,%d)";
	
	public static void addLoginHistory(Request r,boolean success){
		String username=ViewUtils.postBodyDecoder(r.body()).get("username");
		String statement=SQLUtils.sqlSafeFormat(ADD_HISTORY_SQL
				,username
				,r.ip()
				,r.userAgent()
				,new Date().getTime()
				,(success?1:0));
		SQLUtils.executeInsert(statement);
	}
}
