package videoshop.controller;

import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.order.Cart;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManager;
import org.salespointframework.order.OrderStatus;
import org.salespointframework.quantity.Units;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import videoshop.model.Customer;
import videoshop.model.CustomerRepository;
import videoshop.model.Disc;

// (｡◕‿◕｡)
// Straight forward?

@Controller
@PreAuthorize("hasRole('ROLE_BOSS')")
public
class BossController {

	private final OrderManager<Order> orderManager;
	private final Inventory<InventoryItem> inventory;
	private final CustomerRepository customerRepository;
	
	public static int quantity = 5;
	public static int sale = 10;

	@Autowired
	public BossController(OrderManager<Order> orderManager, Inventory<InventoryItem> inventory,
			CustomerRepository customerRepository) {

		this.orderManager = orderManager;
		this.inventory = inventory;
		this.customerRepository = customerRepository;
	}

	@RequestMapping("/customers")
	public String customers(ModelMap modelMap) {

		modelMap.addAttribute("customerList", customerRepository.findAll());

		return "customers";
	}
	@RequestMapping(value = "/stockEdit", method = RequestMethod.POST)
	public String deleteItem(@RequestParam("pid") Disc disc, @ModelAttribute Cart cart, ModelMap modelMap) {
		//cart.removeItem(disc.getIdentifier().toString());
		if (inventory.findByProduct(disc).isPresent()){
		inventory.delete( inventory.findByProduct(disc).get());
		}
		modelMap.addAttribute("customerList", customerRepository.findAll());
		
		return "redirect:stock";
		
	}

	@RequestMapping("/orders")
	public String orders(ModelMap modelMap) {

		modelMap.addAttribute("ordersCompleted", orderManager.find(OrderStatus.COMPLETED));

		return "orders";
	}

	@RequestMapping("/stock")
	public String stock(ModelMap modelMap) {

		modelMap.addAttribute("sale",this.sale);
		modelMap.addAttribute("quantity", this.quantity);
		modelMap.addAttribute("stock", inventory.findAll());

		return "stock";
	}
	
	@RequestMapping(value = "/stock", method = RequestMethod.POST)
	public String saveSettings(@RequestParam("sale") int sale,@RequestParam("quantity") int quantity,ModelMap modelMap) {
		this.sale = sale;
		this.quantity = quantity;
		modelMap.addAttribute("sale",this.sale);
		modelMap.addAttribute("quantity", this.quantity);
		modelMap.addAttribute("stock", inventory.findAll());
		return "stock";
	}
}
