package computech.model;

import javax.persistence.*;

/**
 * Created by Anna on 15.11.2015.
 */

@Entity
public class Reparation {

    @Id
    @GeneratedValue
    private Long id;



    @OneToOne

    private Article article;
    private String description;

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
}
