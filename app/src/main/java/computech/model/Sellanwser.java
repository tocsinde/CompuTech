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
	private double priceoffer;
	
	@ManyToOne
	private Customer customer;
	
	private Sellanwser() {}
	
	public Sellanwser(Customer customer, Article article, String anwser, double priceoffer){
		this.customer = customer;
		this.article = article;
		this.anwser = anwser;
		this.priceoffer = priceoffer;
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
	
	public double getPriceoffer() {
		return priceoffer;
	}

}
