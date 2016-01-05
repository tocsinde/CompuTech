package computech.controller;

import computech.model.*;
import computech.model.validation.ReparationForm;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

/**
 * Created by Anna on 15.11.2015.
 *  */


@PreAuthorize("hasAnyRole('ROLE_PCUSTOMER','ROLE_BOSS')")
@Controller
public class SupportController {

    private final CustomerRepository customerRepository;
    private final ComputerCatalog computerCatalog;
    private final RepairRepository repairRepository;
    @Autowired
    public SupportController(CustomerRepository customerRepository, ComputerCatalog computerCatalog, RepairRepository repairRepository){

        Assert.notNull(customerRepository, "CustomerRepository must not be null!");
        Assert.notNull(computerCatalog, "ComputerCatalog must not be null!");
        Assert.notNull(repairRepository, "RepairRepository must not be null!");

        this.customerRepository = customerRepository;
        this.computerCatalog = computerCatalog;
        this.repairRepository = repairRepository;

    }

    @RequestMapping(value = "/support")
    public String showSupportFormular(ModelMap modelMap){
        modelMap.addAttribute("reparationForm", new ReparationForm());

            modelMap.addAttribute("types", Article.ArticleType.values());

        for (Article.ArticleType type : Article.ArticleType.values()) {
            modelMap.addAttribute(type.toString(), computerCatalog.findByType(type));
        }


      //  modelMap.addAttribute("catalog", computerCatalog.findByType());
      //  modelMap.addAttribute("articleList",  computerCatalog.findAll());



        System.out.println(1);
        return "support";
}

    @RequestMapping(value = "/support/{type}")

    public String showSupportFormular(@PathVariable("type") Article.ArticleType articleType, ModelMap modelMap){
        modelMap.addAttribute("reparationForm", new ReparationForm());
        modelMap.addAttribute("types", Article.ArticleType.values());
        modelMap.addAttribute("selectedType", articleType);
        modelMap.addAttribute("articles", computerCatalog.findByType(articleType));

        //  modelMap.addAttribute("catalog", computerCatalog.findByType());
        //  modelMap.addAttribute("articleList",  computerCatalog.findAll());
        System.out.println(12);



        return "support";
    }


    @RequestMapping(value = "/support", method = RequestMethod.POST)
    public String specification(ModelMap modelMap, @ModelAttribute("reparationForm") @Valid ReparationForm reparationForm, BindingResult result, @LoggedIn Optional<UserAccount> userAccount){
        System.out.print(4);

        modelMap.addAttribute("types", Article.ArticleType.values());
        for (Article.ArticleType type : Article.ArticleType.values()) {
            modelMap.addAttribute(type.toString(), computerCatalog.findByType(type));
        }

        if(result.hasErrors()){
            return "support";
        }
        System.out.println(3);
        modelMap.addAttribute("article", reparationForm.getArticle());
        modelMap.addAttribute("description", reparationForm.getDescription());
        Customer customer = customerRepository.findByUserAccount(userAccount.get());
        modelMap.addAttribute("customer",customer);
        Reparation rep = new Reparation(customer, reparationForm.getArticle(), reparationForm.getDescription());

        repairRepository.save(rep);
        System.out.println(123);


        return "redirect:/support_confirmation";
    }

    @RequestMapping(value = "/support_confirmation")
    public String confirmation(@LoggedIn Optional<UserAccount> userAccount,
                               ModelMap modelMap) {

        /*Reparation rep = null;
        Iterator<Reparation> iterator = repairRepository.findAll().iterator();
        while (iterator.hasNext()) {
            rep = iterator.next();
        }

        customer = rep.getCustomer();*/

        Customer customer = customerRepository.findByUserAccount(userAccount.get());
        modelMap.addAttribute("customer", customer);

        return "support_confirmation";
    }

}
