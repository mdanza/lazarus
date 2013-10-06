package services.authentication;

import javax.ejb.Stateless;

import model.User;

@Stateless(name = "AuthenticationService")
public class AuthenticationServiceImpl implements AuthenticationService{

	public User authenticate(String token) {
		// TODO
		return null;
	}

	public String authenticate(User user) {
		// TODO
		return null;
	}
}
