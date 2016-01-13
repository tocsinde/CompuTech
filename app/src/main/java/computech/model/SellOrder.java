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
	public Long id;
	
	private ArticleType articletype;

	@OneToOne
	private Article article;
	private String description;
	private String condition;
	
	@ManyToOne 
	private Customer customer;
	
	private boolean status = true;

	private SellOrder() {}
	
	public SellOrder(Customer customer, ArticleType articletype, Article article, String description, String condition, boolean status){ 
	   this.customer = customer;
	   this.articletype = articletype;
	   this.article = article;
	   this.description = description;
	   this.condition = condition;
	   this.status = status;
   }
	
	public Long getID() {
		return id;
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
	
	public boolean getStatus() {
		return status;
	}
	
	public void setStatus(boolean status) {
		this.status = status;
	}

}
	