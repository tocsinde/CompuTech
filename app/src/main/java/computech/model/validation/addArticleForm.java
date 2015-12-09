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
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class addArticleForm {

	@NotEmpty(message = "Der Artikelname darf nicht leer sein.")
	private String name;

	private String type;

	@NotEmpty(message = "Das Modell darf nicht leer sein.")
	private String model;

	@NotNull(message = "Der Preis darf nicht leer sein.")
	private BigDecimal price;

	@NotNull(message = "Die Anzahl muss größer als 0 sein.")
	private int quantity;

	public String getModel() {
		return model;
	}

	public String getName() {
		return name;
	}

	public int getQuantity() {
		return quantity;
	}

	public String getType() {
		return type;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public void setType(String type) {
		this.type = type;
	}
}
