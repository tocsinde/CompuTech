package computech.model;

import javax.persistence.*;
import java.math.BigDecimal;

import computech.model.Article;

@Entity
public class Sellanwser {
	
	@Id
	@GeneratedValue
	public Long id;
	
	@OneToOne
	private Article article;
	private String anwser;
	private BigDecimal priceoffer;
	
	@ManyToOne
	private Customer customer;
	
	
	private Long id_costumer;
	
	@SuppressWarnings("unused")
	private Sellanwser() {
		
	}
	
	public Sellanwser(Customer customer, Article article, String anwser, BigDecimal priceoffer, Long id_costumer){
		this.customer = customer;
		this.article = article;
		this.anwser = anwser;
		this.priceoffer = priceoffer;
		this.id_costumer = id_costumer;
	}
	
	public Long getID() {
		return id;
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
	
	public BigDecimal getPriceoffer() {
		return priceoffer;
	}
	
	public Long getId_Costumer() {
		return id_costumer;
	}

}
