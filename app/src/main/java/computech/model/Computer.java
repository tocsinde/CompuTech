package computech.model;

import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.springframework.util.Assert;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * Basic class for all-in-one computer, basically similar to "Article" class.
 * Consists of the basic computer and four more free customizable parts.
 *
 */
@Entity
public class Computer extends Product {


       public  enum Computertype{
        COMPUTER
    }


    private String model;

    private String image;
    private Computertype type;
    @OneToMany(cascade = CascadeType.ALL) private List<Part> prozessor = new LinkedList<Part>();
    @OneToMany(cascade = CascadeType.ALL) private List<Part> graka = new LinkedList<Part>();
    @OneToMany (cascade = CascadeType.ALL)private List<Part> hdd= new LinkedList<Part>();
    @OneToMany(cascade = CascadeType.ALL) private List<Part> ram = new LinkedList<Part>();
    @OneToMany(cascade = CascadeType.ALL) private List<Comment> comments = new LinkedList<Comment>();


    private Computer() {}

    public Computer(String name, String image, Money price, String model,Computertype type) {
        super(name, price);

        this.image = image;
        this.model = model;
        this.type = type;

    }
    public String getModel() {
        return model;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public Iterable<Comment> getComments() {
        return comments;
    }

    public String getImage() {
        return image;
    }

    public Computertype getType() {
        return type;
    }

    public List<Part> getProzessor() {
        return prozessor;
    }

    public List<Part> getGraka() {
        return graka;
    }

    public List<Part> getHdd() {
        return hdd;
    }

    public List<Part> getRam() {
        return ram;
    }
    public void setProzessor(Part prozesso) {
        prozessor.add(prozesso);
    }

    public void setGraka(Part gra) {
        graka.add(gra);
    }

    public void setHdd(Part hard) {
       hdd.add(hard);
    }

    public void setRam(Part r) {
      ram.add(r);
    }
}

