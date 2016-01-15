package computech.TestData;

import computech.model.Article;
import computech.model.Part;
import org.javamoney.moneta.Money;

/**
 * Created by Anwender on 13.01.2016.
 */
public class PartBuilder {



    String name = "";
    String model ="";
    String image ="";
    Part.PartType type = Part.PartType.PROCESSOR;
    Money price = Money.of(0,"EUR");

    public PartBuilder withname(String name){

        this.name=name;
        return this;
    }
    public PartBuilder withimage(String image){

        this.image=image;
        return this;
    }
    public PartBuilder withprice(Money price){

        this.price=price;
        return this;
    }
    public PartBuilder withmodel(String model){

        this.model=model;
        return this;
    }

    public PartBuilder withtype(Part.PartType PartType){

        this.type=PartType;
        return this;
    }

    public Part build() {
        return new Part(name,image,price,model,type);
    }

}
