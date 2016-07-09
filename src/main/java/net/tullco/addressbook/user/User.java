package net.tullco.addressbook.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;

import net.tullco.addressbook.utils.SQLUtils;

public class User {
	private int id=0;
	private String username;
	private String hashedPassword;
	private String salt;
	private boolean admin=false;
	
	private final static String USER_LOADER_SQL="SELECT * FROM users WHERE username=%s";
	private final static String USER_UPDATE_SQL="UPDATE users SET username=%s, password_hash=%s, password_salt=%s WHERE id=%d";
	private final static String USER_INSERT_SQL="INSERT INTO users (username,password_hash,password_salt) VALUES (%s,%s,%s)";
	private final static String ADMIN_DELETE_SQL="DELETE FROM users WHERE admin=1 AND id <> %d";
	private final static String ADMIN_SET_SQL="UPDATE users SET admin=1 WHERE id=%d";
	
	private User(Map<String,String> dataMap){
		setValuesFromMap(dataMap);
	}
	private void setValuesFromMap(Map<String,String> dataMap){
		for(String key:dataMap.keySet()){
			if(key.equals("id"))
				this.id=Integer.parseInt(dataMap.get(key));
			if(key.equals("username"))
				this.username=dataMap.get(key);
			if(key.equals("password_hash"))
				this.hashedPassword=dataMap.get(key);
			if(key.equals("password_salt"))
				this.salt=dataMap.get(key);
			if(key.equals("admin"))
				this.admin=(dataMap.get(key).equals("1")?true:false);
		}
	}
	public String getUsername(){
		return this.username;
	}
	public boolean isAdmin(){
		return this.admin;
	}
	public boolean checkPassword(String pw){
		String hashedPassword = BCrypt.hashpw(pw,this.salt);
		return (hashedPassword.equals(this.hashedPassword)?true:false);
	}
	public void changePassword(String new_pw){
		this.salt=BCrypt.gensalt();
		this.hashedPassword=BCrypt.hashpw(new_pw,this.salt);
	}
	public static User UserLoader(String username){
		String statement=SQLUtils.sqlSafeFormat(USER_LOADER_SQL, username);
		ResultSet rs = SQLUtils.executeSelect(statement);
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
		user.put("admin",(rs.getString("admin")));
		user.put("id", Integer.toString(rs.getInt("id")));
		return user;
	}
	/**
	 * Saves the user to the database. If the user did not exist, it creates a record
	 * for them and sets the id of the object to the database ID.
	 * If the user already exists, then it updates their existing parameters.
	 * @return True if there were no problems. False if an undetermined problem occured.
	 */
	public boolean save(){
		try{
			if(this.id==0){
				String statement=SQLUtils.sqlSafeFormat(USER_INSERT_SQL
						,this.username
						,this.hashedPassword
						,this.salt);
				this.id = SQLUtils.executeInsert(statement);
				return (this.id == 0 ? false : true);
			}else{
				String statement=SQLUtils.sqlSafeFormat(USER_UPDATE_SQL
						,this.username
						,this.hashedPassword
						,this.salt
						,this.id);
				return SQLUtils.executeUpdate(statement);
			}
		}finally{
			if (this.admin){
				SQLUtils.executeUpdate(SQLUtils.sqlSafeFormat(ADMIN_DELETE_SQL, this.id));
				SQLUtils.executeUpdate(SQLUtils.sqlSafeFormat(ADMIN_SET_SQL, this.id));
			}
		}
	}
	/**
	 * Creates a new user with the given username and password.
	 * If the username given already exists, it updates the
	 * password of the existing user with the same username.
	 * @param username The username for the new user
	 * @param password The password for the new user
	 * @return The user that was created/edited.
	 */
	public static User newUser(String username,String password){
		User u;
		if((u=UserLoader(username)) != null){
			u=UserLoader(username);
			u.changePassword(password);
			u.save();
			return u;
		}
		HashMap<String,String> userMap = new HashMap<String,String>();
		String salt=BCrypt.gensalt();
		userMap.put("username", username);
		userMap.put("password_hash", BCrypt.hashpw(password, salt));
		userMap.put("password_salt", salt);
		u = new User(userMap);
		u.save();
		return u;
		
	}
	/**
	 * This function creates a new admin user in the database. It is called during
	 * initial server configuration. Note that all other admin users will be deleted
	 * from the database during the creation of this one. This is the only way to
	 * make an admin user.
	 * @param username The desired username for the admin user
	 * @param password The desired password for the admin user
	 */
	protected static void createAdminUser(String username,String password){
		User u;
		if((u=UserLoader(username))!=null){
			u.changePassword(password);
		}else
			u = User.newUser(username, password);
		//System.out.println(username);
		//System.out.println(u.username);
		u.admin=true;
		u.save();
	}
}
