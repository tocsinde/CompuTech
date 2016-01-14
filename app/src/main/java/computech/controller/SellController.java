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
	
	
	@RequestMapping(value = "/sell", method = RequestMethod.POST)
	public String addtoResell(@ModelAttribute("sellForm") @Valid SellForm sellForm, BindingResult result,  @LoggedIn Optional<UserAccount> userAccount, ModelMap modelmap) {
		System.out.println("Hallo");
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
		
		System.out.println(sellRepository.count());
		System.out.println(sellForm.getArticleType());
		System.out.println(sellForm.getArticle());
		System.out.println(sellForm.getDescription());
		System.out.println(sellForm.getCondition());
		System.out.println(sellForm.getStatus());
		System.out.println(customer);
		
			return "redirect:/sellconfirmation";
	}
	
	@RequestMapping("/sellconfirmation")
	public String getSellanwser(Model model, ModelMap modelmap, @LoggedIn Optional<UserAccount> userAccount) {
	
		Customer customer_logged_in = customerRepository.findByUserAccount(userAccount.get());
		for (Long i=1l; i <= sellanwserRepository.count() ; i++) {
			if ( customer_logged_in == sellanwserRepository.findOne(i).getCustomer()) {
				
				modelmap.addAttribute("sellanwserCompleted", sellanwserRepository.findOne(i));
			}
		}		
		
		return "sellconfirmation";
	}
	
	@RequestMapping(value = "/sellconfirmation", method = RequestMethod.POST)
	public String acceptsellorder(@RequestParam("article") Article article, ModelMap modelmap) {
		
		Optional<InventoryItem> item = inventory.findByProductIdentifier(article.getIdentifier());		
		
		InventoryItem i = item.get();
		i.increaseQuantity(Quantity.of(1));
		inventory.save(i);
		
		
		return "redirect:/";
	}

}
