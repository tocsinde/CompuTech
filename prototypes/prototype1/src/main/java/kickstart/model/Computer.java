package kickstart.model;

import java.util.LinkedList;
//import org.salespointframework.quantity.Units;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
//import org.salespointframework.quantity.Units;
import org.salespointframework.quantity.Quantity;

@Entity
public class Computer extends Product {
	

		public static enum ComputerType {
			NOTEBOOK, COMPUTER;
		}


		private String model;
		private String image;
		private ComputerType type;

	
		//@OneToMany für JPA
		// cascade gibt an, was mit den Kindelementen (Comment) passieren soll wenn das Parentelement (Disc) mit der Datenbank
		// "interagiert"
		@OneToMany(cascade = CascadeType.ALL) private List<Comment> comments = new LinkedList<Comment>();

		// (｡◕‿◕｡)
		// Ein paremterloser public oder protected Konstruktor ist zwingend notwendig für JPA,
		// damit dieser nicht genutzt wird, markieren wir in mit @Deprecated
		@Deprecated
		protected Computer() {}

		public Computer(String name, String image, Money price, String model, ComputerType type) {
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

		public ComputerType getType() {
			return type;
		}
	}
