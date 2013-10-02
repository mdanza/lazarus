package services.authentication;

import javax.ejb.Local;

import model.User;

@Local
public interface AuthenticationService {
	public User authenticate(String token);

	public String authenticate(User user);
}
