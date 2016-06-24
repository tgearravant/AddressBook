package net.tullco.addressbook.user;

public class UserController {
	public static boolean authenticate(String username, String password){
		if (username.isEmpty() || password.isEmpty())
			return false;
		User u = User.UserLoader(username);
		if (u==null)
			return false;
		return u.checkPassword(password);
	}
}
