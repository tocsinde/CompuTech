package computech.model;

import javax.persistence.*;

import computech.model.Article;

@Entity
public class Sellanwser {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@OneToOne
	private Article article;
	private String anwser;
	
	@ManyToOne
	private Customer customer;
	
	private Sellanwser() {}
	
	public Sellanwser(Customer customer, Article article, String anwser){
		this.customer = customer;
		this.article = article;
		this.anwser = anwser;
	}
	
	public Customer getCustomer() {
		return customer;
	}
	
	public Article getArticle() {
		return article;
	}
	
	public String getAnwser() {
		return anwser;
	}

}
