package computech.controller;

import computech.model.Article;
import computech.model.ComputerCatalog;
import computech.model.Customer;
import computech.model.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by Anna on 15.11.2015.
 *  */

@Controller
public class SupportController {
    private final CustomerRepository customerRepository;
    private final ComputerCatalog computerCatalog;
    @Autowired
    public SupportController(CustomerRepository customerRepository, ComputerCatalog computerCatalog){

        Assert.notNull(customerRepository, "CustomerRepository must not be null!");
        Assert.notNull(computerCatalog, "ComputerCatalog must not be null!");

        this.customerRepository = customerRepository;
        this.computerCatalog = computerCatalog;

    }

    @RequestMapping(value = "/support")
    public String showSupportFormular(ModelMap modelMap){

            modelMap.addAttribute("types", Article.ArticleType.values());

        for (Article.ArticleType type : Article.ArticleType.values()) {
            modelMap.addAttribute(type.toString(), computerCatalog.findByType(type));
        }


      //  modelMap.addAttribute("catalog", computerCatalog.findByType());
      //  modelMap.addAttribute("articleList",  computerCatalog.findAll());




        return "support";
}

    @RequestMapping(value = "/support/{type}")

    public String showSupportFormular(@PathVariable("type") Article.ArticleType articleType, ModelMap modelMap){

        modelMap.addAttribute("types", Article.ArticleType.values());


            modelMap.addAttribute("articles", computerCatalog.findByType(articleType));

        //  modelMap.addAttribute("catalog", computerCatalog.findByType());
        //  modelMap.addAttribute("articleList",  computerCatalog.findAll());

        return "support";
    }


    @RequestMapping(value = "result", method = RequestMethod.POST)
    public String specification(ModelMap modelMap,
                                @RequestParam("computer") Article article,
                                @RequestParam("customer") Customer customer,
                                @RequestParam("description") String description){
        modelMap.addAttribute("article", article);
        modelMap.addAttribute("customer", customer);

        return "result";
    }
}
