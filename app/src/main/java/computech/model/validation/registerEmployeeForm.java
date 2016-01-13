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

import javax.validation.constraints.Pattern;

/**
 *
 * Validates a register employee form.
 *
 */

public class registerEmployeeForm {


	@Pattern(regexp="^[a-z|A-Z|\\d]*$", message = "Der Nickname ist ungültig (keine Sonderzeichen erlaubt).")
	@NotEmpty(message = "Der Nickname darf nicht leer sein.")
	private String nickname;

	@NotEmpty(message = "Das Passwort darf nicht leer sein.")
	private String password;

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
