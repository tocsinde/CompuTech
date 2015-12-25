/*
 *
 *	SWT-Praktikum TU Dresden 2015
 *	Gruppe 32 - Computech
 *
 *	Stephan Fischer
 *  Anna Gromykina
 *  Kevin Horst
 *  Philipp Oehme
 *
 */

package computech.model.validation;

import org.hibernate.validator.constraints.NotEmpty;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountIdentifier;
import org.springframework.data.annotation.Transient;

import javax.validation.constraints.AssertTrue;

public class RegistrationForm {

	@NotEmpty(message = "Der Nickname darf nicht leer sein.")
	private String nickname;

	@NotEmpty(message = "Der Vorname darf nicht leer sein.")
	private String firstname;

	@NotEmpty(message = "Der Nachname darf nicht leer sein.")
	private String lastname;

	@NotEmpty(message = "Das Passwort darf nicht leer sein.")
	private String password;

	@Transient
	@NotEmpty(message = "Die Passwortwiederholung darf nicht leer sein.")
	private String password2;

	@AssertTrue(message ="Die Passwörter müssen übereinstimmen.")
	private boolean isValid() {return this.password.equals(this.password2);}

	@NotEmpty(message = "Die Adresse darf nicht leer sein.")
	private String address;

	@NotEmpty(message = "Die E-Mail-Adresse darf nicht leer sein.")
	private String mail;

	@NotEmpty(message = "Die Telefonnummer darf nicht leer sein.")
	private String phone;

	private String role;

	private String connectedEmployee;

	public String getRole() { return role; }

	public void setRole(String role) { this.role = role; }

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword2() {
		return password2;
	}

	public void setPassword2(String password2) {
		this.password2 = password2;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setConnectedEmployee(String connectedEmployee) { this.connectedEmployee = connectedEmployee; }

	public String getConnectedEmployee() { return connectedEmployee; }
}
