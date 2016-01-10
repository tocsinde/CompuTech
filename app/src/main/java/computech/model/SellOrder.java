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

import javax.persistence.*;


@Entity
public class SellOrder{
	
	@Id
	@GeneratedValue
	private Long id;
	
	private ArticleType articletype;

	@OneToOne
	private Article article;
	private String description;
	private String condition;
	
	@ManyToOne 
	private Customer customer;

	private SellOrder() {}
	
	public SellOrder(Customer customer, ArticleType articletype, Article article, String description, String condition){ 
	   this.customer = customer;
	   this.articletype = articletype;
	   this.article = article;
	   this.description = description;
	   this.condition = condition;
   }
	
	public ArticleType getArticleType() {
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
	