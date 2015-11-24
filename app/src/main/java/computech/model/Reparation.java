package computech.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Created by Anna on 15.11.2015.
 */

@Entity
public class Reparation {

    private @Id
    @GeneratedValue
    Long id;


    private Article article;
    private String description;

    @ManyToOne private Customer customer;

    public Reparation(Customer customer, Article article, String description){
        this.customer = customer;
        this.article = article;
        this.description = description;
    }

    public Long getId() {
        return id;
    }


 //   public long getId() {return this.getIdentifier().;}
}
