package model;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import services.authentication.security.PasswordHash;

@Entity
@Table(name = "users")
@NamedQueries({
		@NamedQuery(name = "User.findByUsername", query = "SELECT u FROM User u WHERE u.username = :username"),
		@NamedQuery(name = "User.findByEmail", query = "SELECT u FROM User u WHERE u.email = :email"), })
public class User {

	public enum Role {
		ADMIN, USER
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String salt;

	private Role role;

	@Column(nullable = false)
	private int cryptographicIterations;

	@Column(unique = true)
	private String email;

	private boolean active;

	public long getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		try {
			hashPassword(password);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		if (email != null && !email.equals(""))
			this.email = email;
		else
			email = null;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSalt() {
		return salt;
	}

	public int getCryptographicIterations() {
		return cryptographicIterations;
	}

	private void hashPassword(String password) throws NoSuchAlgorithmException,
			InvalidKeySpecException {
		byte[] salt = PasswordHash.generateRandomSalt();
		int iterations = PasswordHash.getIterationCount();
		this.password = PasswordHash.createHash(password, salt, iterations);
		this.salt = PasswordHash.toHex(salt);
		this.cryptographicIterations = iterations;
	}

}
