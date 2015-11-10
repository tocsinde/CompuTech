package kickstart.model;

import java.util.LinkedList;
import org.salespointframework.quantity.Units;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.quantity.Units;

@Entity
public class Computer extends Product {
	

		public static enum ComputerType {
			NOTEBOOK, COMPUTER;
		}

		// (｡◕‿◕｡)
		// primitve Typen oder Strings müssen nicht extra für JPA annotiert werden
		private String model;
		private String image;
		private ComputerType type;

		// (｡◕‿◕｡)
		// Jede Disc besitzt mehrere Kommentare, eine "1 zu n"-Beziehung -> @OneToMany für JPA
		// cascade gibt an, was mit den Kindelementen (Comment) passieren soll wenn das Parentelement (Disc) mit der Datenbank
		// "interagiert"
		@OneToMany(cascade = CascadeType.ALL) private List<Comment> comments = new LinkedList<Comment>();

		// (｡◕‿◕｡)
		// Ein paremterloser public oder protected Konstruktor ist zwingend notwendig für JPA,
		// damit dieser nicht genutzt wird, markieren wir in mit @Deprecated
		@Deprecated
		protected Computer() {}

		public Computer(String name, String image, Money price, String model, ComputerType type) {
			super(name, price, Units.METRIC);
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

		// (｡◕‿◕｡)
		// Es ist immer sinnvoll sich zu überlegen wie speziell der Rückgabetyp sein sollte
		// Da sowies nur über die Kommentare iteriert wird, ist ein Iterable<T> das sinnvollste.
		// Weil wir keine Liste zurück geben, verhindern wir auch, dass jemand die comments-Liste einfach durch clear() leert.
		// Deswegen geben auch so viele Salespoint Klassen nur Iterable<T> zurück ;)
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
