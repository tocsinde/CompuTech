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

import computech.model.Article;
import computech.model.ComputerCatalog;
import computech.model.Customer;
import computech.model.CustomerRepository;

import java.util.Optional;

import org.salespointframework.catalog.Product;
import org.salespointframework.core.AbstractEntity;
import org.salespointframework.order.Cart;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManager;
import org.salespointframework.payment.Cash;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@PreAuthorize("hasAnyRole('ROLE_PCUSTOMER', 'ROLE_BOSS', 'ROLE_EMPLOYEE')")
@SessionAttributes("sell")
public class SellController {
	
	private final ComputerCatalog computerCatalog;
	private final OrderManager<Order> sellManager;
	private final CustomerRepository customerRepository;
	

	@Autowired
	public SellController( ComputerCatalog computerCatalog, OrderManager<Order> sellManager, CustomerRepository customerRepository) {

		Assert.notNull(sellManager, "sellManager must not be null!");
		Assert.notNull(computerCatalog, "sellManager must not be null!");
		
		this.sellManager = sellManager;
		this.computerCatalog = computerCatalog;
		this.customerRepository = customerRepository;
	}
	
	@RequestMapping(value = "/sell")
    public String showSellFormular(ModelMap modelMap){

            modelMap.addAttribute("articletypes", Article.ArticleType.values());

        for (Article.ArticleType type : Article.ArticleType.values()) {
            modelMap.addAttribute(type.toString(), computerCatalog.findByType(type));
        }


      //  modelMap.addAttribute("catalog", computerCatalog.findByType());
      //  modelMap.addAttribute("articleList",  computerCatalog.findAll());




        return "sell";
	}
	 
	@ModelAttribute("sell")
	public Cart initializeSell() {
		return new Cart(); 
	}
	@RequestMapping(value = "/sell", method = RequestMethod.POST)
	public String addSellArticel(@RequestParam("Article") Article article, int number, @ModelAttribute Cart sell) {

		int amount = 1;

		sell.addOrUpdateItem(article, Quantity.of(amount));

		switch (article.getType()) {
			case NOTEBOOK:
				return "redirect:notebookCatalog";
			case COMPUTER:
				return "redirect:computerCatalog";
			case SOFTWARE:
				return "redirect:softwareCatalog";
			default:
				return "redirect:zubeCatalog";
		}
	} 

	@RequestMapping(value = "/sellorders", method = RequestMethod.GET)
	public String sellorder() {
		return "sellorders";
	}
	
	@RequestMapping(value = "/sellout", method = RequestMethod.POST)
	public String sendrequest(@ModelAttribute Cart sell, @LoggedIn Optional<UserAccount> userAccount) {

		return userAccount.map(account -> {

			Order order = new Order(account, Cash.CASH);

			sell.addItemsTo(order);

			sellManager.payOrder(order);
			sellManager.completeOrder(order);

			sell.clear();

			return "redirect:/";
		}).orElse("redirect:/sell");
	} 
	
}
