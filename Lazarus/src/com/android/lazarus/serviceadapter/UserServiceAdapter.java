package com.android.lazarus.serviceadapter;

public interface UserServiceAdapter {

	/**
	 * @return token in case of success, null otherwise
	 */
	public String login(String username, String password);

	/**
	 * @return true in case of success, false otherwise
	 */
	public boolean register(String username, String password, String email);

	public boolean deactivateUser(String token);

	public boolean modifyPassword(String token, String newPassword);

	public boolean usernameInUse(String username);

	public boolean emailInUse(String email);

}
