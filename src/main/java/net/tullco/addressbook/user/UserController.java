package net.tullco.addressbook.user;

public class UserController {
	/**
	 * This function authenticates a given username and password against the database.
	 * @param username The username of the user being authenticated.
	 * @param password The password corresponding to the username.
	 * @return True if the username and password is valid. False otherwise.
	 */
	public static boolean authenticate(String username, String password){
		if (username == null || password == null || username.isEmpty() || password.isEmpty())
			return false;
		User u = User.userLoader(username);
		if (u==null)
			return false;
		return u.checkPassword(password);
	}
	/**
	 * This function creates a new admin user in the database. It is called during
	 * initial server configuration. Note that all other admin users will be deleted
	 * from the database during the creation of this one. This is the only way to
	 * make an admin user.
	 * @param username The desired username for the admin user
	 * @param password The desired password for the admin user
	 */
	public static void createAdmin(String username,String password){
		System.out.println("Username at controller: "+username);
		User.createAdminUser(username, password);
	}
}
