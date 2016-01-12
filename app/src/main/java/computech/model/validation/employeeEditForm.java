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

/**
 *
 * Was used to validate employee edit form, now it only sets the new password.
 *
 */

package computech.model.validation;

/**
 *
 * Validates an employee edit form.
 *
 */

public class employeeEditForm {

	private String password;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
