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

package computech.model;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.LinkedList;
import java.util.List;

//import org.salespointframework.quantity.Units;

@Entity
public class Part extends Product {


		public  enum PartType {
			PROCESSOR, RAM, GRAPHC, HARDD;
		}


		private String model;
		private String image;
		private PartType type;

		@OneToMany(cascade = CascadeType.ALL) private List<Comment> comments = new LinkedList<Comment>();

		@SuppressWarnings("unused")
		private Part() {}

		public Part(String name, String image, Money price, String model, PartType type) {
			//super(name, price, Units.METRIC);
			super(name, price);
			this.image = image;
			this.model = model;
			this.type = type;
		}

		public String getModel() {
			return model;
		}

		public void addComment(Comment comment) {
			comments.add(comment);
		}
		
		public Iterable<Comment> getComments() {
			return comments;
		}

		public String getImage() {
			return image;
		}

		public PartType getType() {
			return type;
		}
	}
