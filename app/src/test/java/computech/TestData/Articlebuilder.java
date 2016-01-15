package computech.TestData;

import computech.model.Article;
import org.javamoney.moneta.Money;

/**
 * Created by Anwender on 13.01.2016.
 */
public class Articlebuilder {



    String name = "";
    String model ="";
    String image ="";
    Article.ArticleType type = Article.ArticleType.NOTEBOOK;
    Money price = Money.of(0,"EUR");

    public Articlebuilder withname(String name){

        this.name=name;
        return this;
    }
    public Articlebuilder withimage(String image){

        this.image=image;
        return this;
    }
    public Articlebuilder withprice(Money price){

        this.price=price;
        return this;
    }
    public Articlebuilder withmodel(String model){

        this.model=model;
        return this;
    }

    public Articlebuilder withtype(Article.ArticleType Articletype){

        this.type=Articletype;
        return this;
    }

    public Article build() {
        return new Article(name,image,price,model,type);
    }

}
