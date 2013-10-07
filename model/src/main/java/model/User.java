package model;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import services.authentication.security.PasswordHash;

@Entity
@Table(name = "users")
@NamedQueries({
		@NamedQuery(name = "User.findByUsername", query = "SELECT u FROM User u WHERE u.username = :username"),
		@NamedQuery(name = "User.findByEmail", query = "SELECT u FROM User u WHERE u.email = :email"),
		@NamedQuery(name = "User.findByCellphone", query = "SELECT u FROM User u WHERE u.cellphone = :cellphone") })
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String salt;

	@Column(nullable = false)
	private int cryptographicIterations;

	@Column(unique = true)
	private String email;

	@Column(unique = true)
	private String cellphone;

	private boolean active;

	@Column(nullable = false)
	private String secretQuestion;

	@Column(nullable = false)
	private String secretAnswer;

	@ManyToOne
	@JoinColumn(name = "role_id")
	private Role role;

	public int getId() {
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
		if (email != "")
			this.email = email;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getCellphone() {
		return cellphone;
	}

	public void setCellphone(String cellphone) {
		if (cellphone != "")
			this.cellphone = cellphone;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getSecretQuestion() {
		return secretQuestion;
	}

	public void setSecretQuestion(String secretQuestion) {
		this.secretQuestion = secretQuestion;
	}

	public String getSecretAnswer() {
		return secretAnswer;
	}

	public void setSecretAnswer(String secretAnswer) {
		this.secretAnswer = secretAnswer;
	}

	public void setId(int id) {
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
