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

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;

public class customerEditForm {

	@NotEmpty(message = "Der Vorname darf nicht leer sein.")
	private String firstname;

	@NotEmpty(message = "Der Nachname darf nicht leer sein.")
	private String lastname;

	@NotEmpty(message = "Die Adresse darf nicht leer sein.")
	private String address;

	@NotEmpty(message = "Die E-Mail-Adresse darf nicht leer sein.")
	@Email(message = "Die E-Mail-Adresse ist ungültig.")
	private String mail;

	@NotEmpty(message = "Die Telefonnummer darf nicht leer sein.")
	@Pattern(regexp="^(\\d+|\\ |\\-|\\(|\\)|\\/)+$", message = "Die Telefonnummer ist ungültig.")
	private String phone;

	private String connectedEmployee;

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
