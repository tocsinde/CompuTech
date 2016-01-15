package computech.model;

import org.javamoney.moneta.Money;

import javax.persistence.*;

/**
 * Created by Anna on 15.11.2015.
 */

@Entity
public class Reparation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne

    private Article article;
    private String description;
    @Lob
    private Money price;

    private boolean paid=false;

    @ManyToOne private Customer customer;

    private Reparation() {}

    public Reparation(Customer customer, Article article, String description){
        this.customer = customer;
        this.article = article;
        this.description = description;
    }

    public Long getId() {
        return id;
    }


 //   public long getId() {return this.getIdentifier().;}
 public Article getArticle() {
     return article;
 }

    public String getDescription() {
        return description;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setPrice(Money price) {
        this.price = price;
    }

    public Money getPrice() {
        return price;
    }

    public boolean isPaid() {return paid;}

    public void setPaid() {this.paid = true;}
}
