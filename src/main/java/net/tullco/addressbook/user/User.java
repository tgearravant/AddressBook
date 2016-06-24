package net.tullco.addressbook.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;

import net.tullco.addressbook.utils.SQLiteUtils;

public class User {
	private int id=0;
	private String username;
	private String hashedPassword;
	private String salt;
	
	private final static String USER_LOADER_SQL="SELECT * FROM users WHERE username=%s";
	private final static String USER_UPDATE_SQL="UPDATE users SET username=%s, password_hash=%s, password_salt=%s WHERE id=%d";
	private final static String USER_INSERT_SQL="INSERT INTO users (username,password_hash,password_salt) VALUES (%s,%s,%s)";
	
	private User(Map<String,String> dataMap){
		setValuesFromMap(dataMap);
	}
	private void setValuesFromMap(Map<String,String> dataMap){
		for(String key:dataMap.keySet()){
			if(key.equals("id"))
				this.id=Integer.parseInt(dataMap.get(key));
			if(key.equals("username"))
				this.username=dataMap.get(username);
			if(key.equals("password_hash"))
				this.hashedPassword=dataMap.get(key);
			if(key.equals("password_salt"))
				this.salt=dataMap.get(key);
		}
	}
	public String getUserName(){
		return this.username;
	}
	public boolean checkPassword(String pw){
		String hashedPassword = BCrypt.hashpw(pw,this.salt);
		return (hashedPassword.equals(this.hashedPassword)?true:false);
	}
	public static User UserLoader(String username){
		String statement=SQLiteUtils.sqlSafeFormat(USER_LOADER_SQL, username);
		ResultSet rs = SQLiteUtils.executeSelect(statement);
		try {
			if(!rs.isBeforeFirst())
				return null;
			rs.next();
			User u=new User(convertResultSetToUserMap(rs));
			rs.close();
			return u;
		} catch (SQLException e) {
			return null;
		}
	}
	private static Map<String,String> convertResultSetToUserMap(ResultSet rs) throws SQLException{
		String[] fields={"username","password_hash","password_salt"};
		HashMap<String,String> user=new HashMap<String, String>();
		for(String s:fields){
			user.put(s, rs.getString(s));
		}
		user.put("id", Integer.toString(rs.getInt("id")));
		return user;
	}
	public boolean save(){
		if(this.id==0){
			String statement=SQLiteUtils.sqlSafeFormat(USER_INSERT_SQL
					, this.username
					,this.hashedPassword
					,this.salt);
			return SQLiteUtils.executeUpdate(statement);
		}else{
			String statement=SQLiteUtils.sqlSafeFormat(USER_UPDATE_SQL
					,this.username
					,this.hashedPassword
					,this.salt
					,this.id);
			return SQLiteUtils.executeUpdate(statement);
		}
	}
	public static User newUser(String username,String password){
		HashMap<String,String> userMap = new HashMap<String,String>();
		String salt=BCrypt.gensalt();
		userMap.put("username", username);
		userMap.put("password_hash", BCrypt.hashpw(password, salt));
		userMap.put("password_salt", salt);
		User user = new User(userMap);
		user.save();
		return user;
		
	}
}
