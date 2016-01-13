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

/**
 *
 * Basic class for notebooks, supplies and software. While name and price are located in Salespoint's "Product" class,
 * model, type and image of the product are stored here.
 *
 */

@Entity
public class Article extends Product {
	

		public  enum ArticleType {
			NOTEBOOK, SOFTWARE, ZUBE;
		}


		private String model;
		private String image;
		private ArticleType type;

		@OneToMany(cascade = CascadeType.ALL) private List<Comment> comments = new LinkedList<Comment>();

		@SuppressWarnings("unused")
		private Article() {}

		public Article(String name, String image, Money price, String model, ArticleType type) {
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

		public ArticleType getType() {
			return type;
		}
	}
