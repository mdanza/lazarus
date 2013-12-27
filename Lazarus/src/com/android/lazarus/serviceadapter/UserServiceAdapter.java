package com.android.lazarus.serviceadapter;

public interface UserServiceAdapter {

	/**
	 * @return token in case of success, null otherwise
	 */
	public String login(String username, String password);

	/**
	 * @return true in case of success, false otherwise
	 */
	public boolean register(String username, String password, String email,
			String cellphone, String secretQuestion, String secretAnswer);

	public boolean usernameInUse(String username);

}
