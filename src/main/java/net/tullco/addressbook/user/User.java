package net.tullco.addressbook.user;

import java.util.Map;

import net.tullco.addressbook.utils.SQLiteUtils;

public class User {
	private int id;
	private String username;
	private String hashedPassword;
	private String salt;
	
	private final String USER_LOADER_SQL="SELECT * FROM users WHERE username=%s";
	
	private User(Map<String,String> dataMap){
		setValuesFromMap(dataMap);
	}
	private void setValuesFromMap(Map<String,String> dataMap){
		for(String key:dataMap.keySet()){
			if(key.equals("id"))
				this.id=Integer.parseInt(dataMap.get(key));
			if(key.equals("username"))
				this.username=dataMap.get(username);
			if(key.equals("hashedPassword"))
				this.hashedPassword=dataMap.get(key);
			if(key.equals("salt"))
				this.salt=dataMap.get(key);
		}
	}
	public String getUserName(){
		return this.username;
	}
	public boolean checkPassword(String pw){
		return true;
	}
	public User UserLoader(String username){
		SQLiteUtils.executeSelect("");
	}
}
