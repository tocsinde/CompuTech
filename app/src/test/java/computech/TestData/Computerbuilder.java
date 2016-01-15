package computech.TestData;

import computech.model.Computer;
import computech.model.Part;
import org.javamoney.moneta.Money;

/**
 * Created by Anwender on 13.01.2016.
 */
public class Computerbuilder {



    String name = "";
    String model ="";
    String image ="";
    Computer.Computertype type = Computer.Computertype.COMPUTER;
    Money price = Money.of(0,"EUR");

    public Computerbuilder withname(String name){

        this.name=name;
        return this;
    }
    public Computerbuilder withimage(String image){

        this.image=image;
        return this;
    }
    public Computerbuilder withprice(Money price){

        this.price=price;
        return this;
    }
    public Computerbuilder withmodel(String model){

        this.model=model;
        return this;
    }

    public Computerbuilder withtype(Computer.Computertype Computertype){

        this.type=Computertype;
        return this;
    }

    public Computer build() {
        return new Computer(name,image,price,model,type);
    }

}
