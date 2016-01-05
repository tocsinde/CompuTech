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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
    private final SellRepository sellRepository;

    @Autowired
    public SupportController(CustomerRepository customerRepository, ComputerCatalog computerCatalog, RepairRepository repairRepository, SellRepository sellRepository){

        Assert.notNull(customerRepository, "CustomerRepository must not be null!");
        Assert.notNull(computerCatalog, "ComputerCatalog must not be null!");
        Assert.notNull(repairRepository, "RepairRepository must not be null!");

        this.customerRepository = customerRepository;
        this.computerCatalog = computerCatalog;
        this.repairRepository = repairRepository;
        this.sellRepository = sellRepository;

    }

    @RequestMapping(value = "/support")
    public String showSupportFormular(ModelMap modelMap){

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

        modelMap.addAttribute("types", Article.ArticleType.values());
        modelMap.addAttribute("selectedType", articleType);


            modelMap.addAttribute("articles", computerCatalog.findByType(articleType));

        //  modelMap.addAttribute("catalog", computerCatalog.findByType());
        //  modelMap.addAttribute("articleList",  computerCatalog.findAll());
        System.out.println(12);



        return "support";
    }


    @RequestMapping(value = "/support", method = RequestMethod.POST)
    public String specification(ModelMap modelMap,
                                @ModelAttribute("ReparationForm") @Valid ReparationForm reparationForm,
                                BindingResult result,
                                @LoggedIn Optional<UserAccount> userAccount,
                                @RequestParam("article") Article article,
                                @RequestParam("description") String description){
        modelMap.addAttribute("article", article);
        modelMap.addAttribute("description", description);
        Customer customer = customerRepository.findByUserAccount(userAccount.get());
        modelMap.addAttribute("customer",customer);
        Reparation rep = new Reparation(customer, article, description);

        repairRepository.save(rep);

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

    @RequestMapping(value = "/support_price_offer")
    public String showPriceOffers(ModelMap modelMap,
                                  @LoggedIn Optional<UserAccount> userAccount){

        Customer customer = customerRepository.findByUserAccount(userAccount.get());

        Iterator<Reparation> iterator = repairRepository.findAll().iterator();
        List<Reparation> reparationList = new ArrayList<Reparation>();
        while (iterator.hasNext()) {
            Reparation reparation = iterator.next();
            if (reparation.getCustomer().getId() == customer.getId()) {
                reparationList.add(reparation);
            }
        }

        modelMap.addAttribute("reparations", reparationList);

        return "support_price_offer";
    }




    @RequestMapping(value = "/support_price_offer", method = RequestMethod.POST)
    public String onPriceOfferDecisionMade(@RequestParam(required = false, value ="accept") String acceptFlag,
                                           @RequestParam(required = false, value ="deny") String denyFlag,
                                           @RequestParam("reparationId") Long reparationId,
                                           @LoggedIn Optional<UserAccount> userAccount){

        Customer customer = customerRepository.findByUserAccount(userAccount.get());

        Reparation reparation = repairRepository.findOne(reparationId);
        System.out.print("1");

        if (acceptFlag != null) {
            System.out.print("2");
            sellRepository.save(new SellOrder(customer, reparation.getArticle().getType(), reparation.getArticle(), reparation.getDescription()));
            repairRepository.delete(reparationId);
        } else if (denyFlag != null) {
            repairRepository.delete(reparationId);
        }


        return "redirect:/support_price_offer";
    }
}


