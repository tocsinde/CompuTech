package computech.controller;

import computech.model.*;
import computech.model.validation.ReparationForm;
import org.salespointframework.order.Cart;
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
 *
 * The SupportController contains most of the reparations functions.
 *
 */

@PreAuthorize("hasAnyRole('ROLE_PCUSTOMER','ROLE_BOSS')")
@Controller
public class SupportController {

    private final CustomerRepository customerRepository;
    private final ComputerCatalog computerCatalog;
    private final RepairRepository repairRepository;
    private final SellRepairRepository sellRepairRepository;

    @Autowired
    public SupportController(SellRepairRepository sellRepairRepository, CustomerRepository customerRepository, ComputerCatalog computerCatalog, RepairRepository repairRepository, SellRepository sellRepository) {

        Assert.notNull(customerRepository, "CustomerRepository must not be null!");
        Assert.notNull(computerCatalog, "ComputerCatalog must not be null!");
        Assert.notNull(repairRepository, "RepairRepository must not be null!");

        this.customerRepository = customerRepository;
        this.computerCatalog = computerCatalog;
        this.repairRepository = repairRepository;
        this.sellRepairRepository = sellRepairRepository;

    }


    /**
     * Shows form for editing a reparation.
     *
     * @param modelMap contains the reparationsform
     * @param modelMap contaisns the article types
     * @return template "support"
     */

    @RequestMapping(value = "/support")
    public String showSupportFormular(ModelMap modelMap) {

        modelMap.addAttribute("reparationForm", new ReparationForm());

        modelMap.addAttribute("types", Article.ArticleType.values());

        for (Article.ArticleType type : Article.ArticleType.values()) {
            modelMap.addAttribute(type.toString(), computerCatalog.findByType(type));
        }


      return "support";
    }


    /**
     * Reload form for editing a reparation.
     *
     * @param modelMap contains the reparationsform
     * @param modelMap contaisns the article types
     * @param modelMap contaisns models of the articles
     * @return template "support"
     */

    @RequestMapping(value = "/support/{type}")

    public String showSupportFormular(@PathVariable("type") Article.ArticleType articleType, ModelMap modelMap) {

        modelMap.addAttribute("reparationForm", new ReparationForm());
        modelMap.addAttribute("types", Article.ArticleType.values());
        modelMap.addAttribute("selectedType", articleType);


        modelMap.addAttribute("articles", computerCatalog.findByType(articleType));

        //  modelMap.addAttribute("catalog", computerCatalog.findByType());
        //  modelMap.addAttribute("articleList",  computerCatalog.findAll());
        System.out.println(12);


        return "support";
    }

    /**
     * Checks reparation form and saves reparations data.
     *
     * @param id ID of the customer who have a reparation
     * @param modelMap contains type of the article
     * @return redirect to template "support_confirmation"
     */


    @RequestMapping(value = "/support", method = RequestMethod.POST)
    public String specification(ModelMap modelMap, @ModelAttribute("reparationForm") @Valid ReparationForm reparationForm, BindingResult result, @LoggedIn Optional<UserAccount> userAccount) {

        modelMap.addAttribute("types", Article.ArticleType.values());

        for (Article.ArticleType type : Article.ArticleType.values()) {
            modelMap.addAttribute(type.toString(), computerCatalog.findByType(type));
        }

        if (result.hasErrors()) {
            return "support";
        }

        modelMap.addAttribute("article", reparationForm.getArticle());
        modelMap.addAttribute("description", reparationForm.getDescription());
        Customer customer = customerRepository.findByUserAccount(userAccount.get());
        modelMap.addAttribute("customer", customer);
        Reparation rep = new Reparation(customer, reparationForm.getArticle(), reparationForm.getDescription());

        repairRepository.save(rep);

        return "redirect:/support_confirmation";
    }

    /**
     * Confirm a reparation.
     *
     * @param id ID of the customer who is locked in
     * @param modelMap contains customer
     * @return template "support_confirmation"
     */

    @RequestMapping(value = "/support_confirmation")
    public String confirmation(@LoggedIn Optional<UserAccount> userAccount,
                               ModelMap modelMap) {

        Customer customer = customerRepository.findByUserAccount(userAccount.get());
        modelMap.addAttribute("customer", customer);

        return "support_confirmation";
    }

    /**
     * Show reparationslist for Boss and employee.
     *
     * @param id ID of the customer who is locked in
     * @param modelMap contains List of the reparations
     * @return template "support_confirmation"
     */
    @RequestMapping(value = "/support_price_offer")
    public String showPriceOffers(ModelMap modelMap,
                                  @LoggedIn Optional<UserAccount> userAccount) {

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

    /**
     * Save price for the reparation
     *
     * modelMap contains a list of enabled employees
     * @param acceptFlag - will be set if customer will be accept the protosition
     * @param denyFlag - will be set if customer will be deny the proposition
     * @param reparationId - id of the reparation, which price will be changed
     * @param modelMap contains customer
     * @param modelMap contains article from the repatation
     * @return redirect to template "support_price_confirmation" if customer accept the price
     * @return redirect to template "support_price_confirmation_not" if customer deny the price
     */


    @RequestMapping(value = "/support_price_offer", method = RequestMethod.POST)
    public String onPriceOfferDecisionMade(@RequestParam(required = false, value = "accept") String acceptFlag,
                                           @RequestParam(required = false, value = "deny") String denyFlag,
                                           @RequestParam("reparationId") Long reparationId,
                                           @LoggedIn Optional<UserAccount> userAccount, ModelMap modelMap) {

        Customer customer = customerRepository.findByUserAccount(userAccount.get());
        Reparation reparation = repairRepository.findOne(reparationId);

        modelMap.addAttribute("customer", customer);
        modelMap.addAttribute("article", reparation.getArticle());

        if (acceptFlag != null) {

            sellRepairRepository.save(reparation);
            repairRepository.delete(reparationId);
            return "support_price_confirmation";
        } else if (denyFlag != null) {
            repairRepository.delete(reparationId);
        }


        return "support_price_confirmation_not";

    }

}

