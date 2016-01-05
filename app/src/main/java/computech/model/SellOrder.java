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

import computech.model.Article.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManager;

@Entity
public class SellOrder{
	
	private @Id
	@GeneratedValue
	Long id;
	
	private ArticleType articletype;
	private Article article;
	private String description;
	private String condition;
	
	@ManyToOne private Customer customer;
	
	public SellOrder(Customer customer, ArticleType articletype, Article article, String description, String condition){ 
	   this.customer = customer;
	   this.articletype = articletype;
	   this.article = article;
	   this.description = description;
	   this.condition = condition;
   }
	
	public ArticleType getArticletype() {
		return articletype;
	}

	public Article getArticle() {
		return article;
	}

	public String getDescription() {
		return description;
	}

	public Customer getCustomer() {
		return customer;
	}	
	
	public String getCondition() {
		return condition;
	}

}
	