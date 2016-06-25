package net.tullco.addressbook.login;

import net.tullco.addressbook.utils.SQLiteUtils;
import net.tullco.addressbook.utils.ViewUtils;
import spark.Request;

public class LoginHistory {
	
	public final static String ADD_HISTORY_SQL = "INSERT INTO login_attempt_history "
			+ "(username,ip_address,user_agent) "
			+ "VALUES (%s,%s,%s)";
	
	public static void addLoginHistory(Request r){
		String username=ViewUtils.postBodyDecoder(r.body()).get("username");
		String statement=SQLiteUtils.sqlSafeFormat(ADD_HISTORY_SQL, username,r.ip(),r.userAgent());
		SQLiteUtils.executeInsert(statement);
	}
}
