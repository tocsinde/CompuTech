/*
 *
 *	SWT-Praktikum TU Dresden 2015
 *	Gruppe 32 - Computech
 *
 *	Stephan Fischer
 *  Anna Gromykina
 *  Kevin Horst
 *  Philipp Oehme
 *
 */

package computech.controller;

import computech.model.*;
import computech.model.validation.SellForm;
import computech.model.validation.SellanwserForm;
import computech.model.validation.customerEditForm;

import java.util.Optional;

import javax.validation.Valid;

import org.salespointframework.quantity.Quantity;

import org.salespointframework.catalog.Product;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * 
 *
 * The Sellcontroller contains the functions for private costumers in order to send a sellorder and receive and send sellanswers
 *
 */
@Controller
@PreAuthorize("hasRole('ROLE_PCUSTOMER')")
@SessionAttributes("sell")
public class SellController {
	
	private static final Quantity NONE = Quantity.of(0);
	private final ComputerCatalog computerCatalog;
	private final CustomerRepository customerRepository;
	private final SellRepository sellRepository;
	private final SellanwserRepository sellanwserRepository;
	private final Inventory<InventoryItem> inventory;

	@Autowired
	public SellController(ComputerCatalog computerCatalog, CustomerRepository customerRepository, SellRepository sellRepository, SellanwserRepository sellanwserRepository, Inventory<InventoryItem> inventory) {
		Assert.notNull(computerCatalog, "computerCatalog must not be null!");
		Assert.notNull(customerRepository, "customerRepository must not be null!");
		Assert.notNull(sellRepository, "sellRepository must not be null!");
		Assert.notNull(sellanwserRepository, "sellanwserRepository must not be null!");
		Assert.notNull(inventory, "inventory must not be null!");
		this.computerCatalog = computerCatalog;
		this.customerRepository = customerRepository;
		this.sellRepository = sellRepository;
		this.sellanwserRepository = sellanwserRepository;
		this.inventory = inventory;
	}

	/**
	 * 
	 * Show the Form to send a sell request
	 * 
	 * @param modelMap contains the sellForm, especially the articletype
	 * @param model contains the customer filling in the form
	 * @param userAccount contains the logged in customer
	 * @return redirect to template "sell"
	 */
	@RequestMapping(value = "/sell")
    public String showSellFormular(ModelMap modelMap, Model model, @LoggedIn Optional<UserAccount> userAccount){
		
		Customer customer = customerRepository.findByUserAccount(userAccount.get());
		model.addAttribute("customer", customer);
		modelMap.addAttribute("sellForm", new SellForm());
        modelMap.addAttribute("articletypes", Article.ArticleType.values());
		model.addAttribute("customer", customer);

        for (Article.ArticleType articleType : Article.ArticleType.values()) {
        	
            modelMap.addAttribute(articleType.toString(), computerCatalog.findByType(articleType));
        }
        
        return "sell";
	}
	
	/**
	 * 
	 * Shows the Form to send a sell request with articletype chosen
	 * 
	 * @param articleType contains the chosen articletype
	 * @param modelMap contains the sellForm and the selected articletype
	 * @param model contains the costumer filling in the form
	 * @param userAccount contains the logged in customer
	 * @return redirect to template "sell"
	 */
	@RequestMapping(value = "/sell/{articleType}")
    public String showSellFormular(@PathVariable("articleType") Article.ArticleType articleType,ModelMap modelMap, Model model, @LoggedIn Optional<UserAccount> userAccount){
		
		Customer customer = customerRepository.findByUserAccount(userAccount.get());
		model.addAttribute("customer", customer);

		modelMap.addAttribute("sellForm", new SellForm());
        modelMap.addAttribute("articletypes", Article.ArticleType.values());
        modelMap.addAttribute("selectedarticleType", articleType);
        modelMap.addAttribute("articles", computerCatalog.findByType(articleType));

        return "sell";
    }
	
	/**
	 * 
	 * For sending a request for selling an article to Computech
	 * 
	 * @param sellForm form thats needs to be validated
	 * @param result validation of the form
	 * @param userAccount contains logged in costumer
	 * @param modelmap contains the sellForm
	 * @return redirect to template "sellconfirmation"
	 */
	@RequestMapping(value = "/sell", method = RequestMethod.POST)
	public String addtoResell(@ModelAttribute("sellForm") @Valid SellForm sellForm, BindingResult result,  @LoggedIn Optional<UserAccount> userAccount, ModelMap modelmap) {

		modelmap.addAttribute("articletypes", Article.ArticleType.values());
		for (Article.ArticleType articleType : Article.ArticleType.values()) {
			 modelmap.addAttribute(articleType.toString(), computerCatalog.findByType(articleType));
		}
		
		if (result.hasErrors()) {
			return "sell";
		}
		
		Customer customer = customerRepository.findByUserAccount(userAccount.get());
		modelmap.addAttribute("customer", customer);
		modelmap.addAttribute("status", true);
		SellOrder sellorder = new SellOrder(customer, sellForm.getArticleType(), sellForm.getArticle(), sellForm.getDescription(), sellForm.getCondition(), sellForm.getStatus());
		sellRepository.save(sellorder); 
		
			return "redirect:/sellconfirmation";
	}
	
	/**
	 * 
	 * Shows the form for receiving sell answers and agreeing with the price
	 *  
	 * @param modelmap contains the completed sell answers 
	 * @param userAccount contains the logged in customer
	 * @return redirect to template "sellconfirmation"
	 */
	@RequestMapping("/sellconfirmation")
	public String getSellanwser(ModelMap modelmap, @LoggedIn Optional<UserAccount> userAccount) {
	
		Customer customer_logged_in = customerRepository.findByUserAccount(userAccount.get());
		for (Long i=1l; i <= sellanwserRepository.count() ; i++) {
			if ( customer_logged_in == sellanwserRepository.findOne(i).getCustomer()) {
				
				modelmap.addAttribute("sellanwserCompleted", sellanwserRepository.findOne(i));
			}
		}		
		
		return "sellconfirmation";
	}
	
	/**
	 * 
	 * Add the article the customer send a sell for to stock
	 * 
	 * @param id ID of the sell answer
	 * @param modelmap contains the completed sell answer
	 * @return redirect to start
	 */
	@RequestMapping(value = "/sellconfirmation", method = RequestMethod.POST)
	public String acceptsellorder(@RequestParam("id") Long id, ModelMap modelmap) {
		
		Article article = sellanwserRepository.findOne(id).getArticle();
		Optional<InventoryItem> item = inventory.findByProductIdentifier(article.getIdentifier());		
		
		InventoryItem i = item.get();
		i.increaseQuantity(Quantity.of(1));
		inventory.save(i);
		
		
		return "redirect:/";
	}

}
