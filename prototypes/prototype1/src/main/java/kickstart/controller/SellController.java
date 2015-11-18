package kickstart.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@PreAuthorize("hasAnyRole('ROLE_PCUSTOMER', 'ROLE_BOSS', 'ROLE_EMPLOYEE')")
@SessionAttributes("sell")
public class SellController {
	
	private final OrderManager<Order> sellManager;
	
	@Autowired
	public SellController(OrderManager<Order> sellManager) {

		Assert.notNull(sellManager, "sellManager must not be null!");
		this.sellManager = sellManager;
	}
	
	@ModelAttribute("sell")
	public Cart initializeSell() {
		return new Cart();
	}
	@RequestMapping(value = "/sell", method = RequestMethod.POST)
	public String addSellArticel(@RequestParam("Computer") Computer Computer, int number, @ModelAttribute Cart sell) {
	
		int amount = 1;
		
		sell.addOrUpdateItem(Computer, Quantity.of(amount));
		
		switch (Computer.getType()) {
		case COMPUTER:
			return "redirect:computerCatalog";
		case NOTEBOOK:
		default:
			return "redirect:notebookCatalog";
	}
	} 
	
	@RequestMapping(value = "/sell", method = RequestMethod.GET)
	public String sellorder() {
		return "sell";
	}
	
/*	@RequestMapping(value = "/checkout", method = RequestMethod.POST)
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
	*/
}
