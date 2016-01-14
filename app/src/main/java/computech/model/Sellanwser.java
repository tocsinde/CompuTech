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
	
	private boolean status = true;
	
	private Long id_costumer;
	
	@SuppressWarnings("unused")
	private Sellanwser() {
		
	}
	
	public Sellanwser(Customer customer, Article article, String anwser, BigDecimal priceoffer, Long id_costumer, boolean status){
		this.customer = customer;
		this.article = article;
		this.anwser = anwser;
		this.priceoffer = priceoffer;
		this.id_costumer = id_costumer;
		this.status = status;
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
	
	public boolean getStatus() {
		return status;
	}
	
	public void setStatus(boolean status) {
		this.status = status;
	}

}
