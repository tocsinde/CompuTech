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
import computech.model.Article.ArticleType;
import computech.model.validation.SellForm;

import java.util.Optional;

import javax.validation.Valid;

import computech.model.validation.registerEmployeeForm;
import org.salespointframework.catalog.Product;
import org.salespointframework.core.AbstractEntity;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.order.Cart;
import org.salespointframework.order.CartItem;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManager;
import org.salespointframework.payment.Cash;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@PreAuthorize("hasAnyRole('ROLE_PCUSTOMER', 'ROLE_BOSS', 'ROLE_EMPLOYEE')")
@SessionAttributes("sell")
public class SellController {
	
	private final ComputerCatalog computerCatalog;
	private final OrderManager<Order> sellManager;
	private final CustomerRepository customerRepository;
	private final SellRepository sellRepository;

	@Autowired
	public SellController( ComputerCatalog computerCatalog, OrderManager<Order> sellManager, CustomerRepository customerRepository, SellRepository sellRepository) {

		Assert.notNull(sellManager, "sellManager must not be null!");
		Assert.notNull(computerCatalog, "computerCatalog must not be null!");
		Assert.notNull(customerRepository, "customerRepository must not be null!");
		Assert.notNull(sellRepository, "sellRepository must not be null!");
		this.sellManager = sellManager;
		this.computerCatalog = computerCatalog;
		this.customerRepository = customerRepository;
		this.sellRepository = sellRepository;
	}

	@RequestMapping(value = "/sell")
    public String showSellFormular(ModelMap modelMap){

		modelMap.addAttribute("sellForm", new SellForm());
            modelMap.addAttribute("articletypes", Article.ArticleType.values());

        for (Article.ArticleType type : Article.ArticleType.values()) {
            modelMap.addAttribute(type.toString(), computerCatalog.findByType(type));
        }
        
        //    modelMap.addAttribute("articletypes", Article.ArticleType.values());

        
        //    	modelMap.addAttribute("articles", computerCatalog.findByType(articletype));

        return "sell";
	}
	
	/*@RequestMapping(value = "/sell/{articletype}")

    public String showSupportFormular(@PathVariable("articletype") Article.ArticleType articletype, ModelMap modelMap){

        modelMap.addAttribute("articletype", Article.ArticleType.values());


            modelMap.addAttribute("articles", computerCatalog.findByType(articletype));

        //  modelMap.addAttribute("catalog", computerCatalog.findByType());
        //  modelMap.addAttribute("articleList",  computerCatalog.findAll());

        return "sell";
    } */
	
	@RequestMapping(value = "/sell", method = RequestMethod.POST)
	private String addtoResell(@ModelAttribute("SellForm") @Valid SellForm SellForm, ModelMap modelmap, Model model, BindingResult result,  @LoggedIn Optional<UserAccount> userAccount) {
		Customer customer = customerRepository.findByUserAccount(userAccount.get());
		if (result.hasErrors()) {
			return "sell";
		}
		
		//ArticleType articletype =SellForm.getArticleType();
		//Article article = SellForm.getArticle();
		//String description = SellForm.getDescription();
		
		SellOrder sellorder = new SellOrder(customer, SellForm.getArticleType(), SellForm.getArticle(), SellForm.getDescription());
		sellRepository.save(sellorder);
		//modelmap.addAttribute("article", article);
		//modelmap.addAttribute("description", description);
		
			return "redirect:/";
	} 
	
/*	@RequestMapping(value = "/sell", method = RequestMethod.POST)
	public String addSellArticel(@RequestBody MultiValueMap body) {

		System.out.println("###################################################################################");
		System.out.println(body);

		return "redirect:/";
	//	addtoResell(article,description);

		switch (article.getType()) {
			case NOTEBOOK:
				return "redirect:/notebookCatalog";
			case COMPUTER:
				return "redirect:/computerCatalog";
			case SOFTWARE:
				return "redirect:/softwareCatalog";
			default:
				return "redirect:/zubeCatalog";
		} 
	}  */

	/*@RequestMapping(value = "/sellout", method = RequestMethod.POST)
	public String sendrequest(@ModelAttribute Cart sell, @LoggedIn Optional<UserAccount> userAccount) {

		return userAccount.map(account -> {

			Order sellorder = new Order(account, Cash.CASH);
			
			sell.addItemsTo(sellorder);

			sellManager.payOrder(sellorder);
			sellManager.completeOrder(sellorder);

			sell.clear();

			return "redirect:/";
		}).orElse("redirect:/sell");
	} */

}
