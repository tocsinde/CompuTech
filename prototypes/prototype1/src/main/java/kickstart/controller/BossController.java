package kickstart.controller;

import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManager;
import org.salespointframework.order.OrderStatus;
import org.salespointframework.useraccount.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import kickstart.model.CustomerRepository;

import javax.management.relation.RoleStatus;

@Controller
@PreAuthorize("hasRole('ROLE_BOSS')")
class BossController {

	private final OrderManager<Order> orderManager;
	private final Inventory<InventoryItem> inventory;
	private final CustomerRepository customerRepository;

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

	//@RequestMapping(value = "/customers/edit", method = RequestMethod.POST)

	@RequestMapping(value = "/customers/delete", method = RequestMethod.POST)
	public String delete() {

		// kunden löschen

		return "gelöscht";
	}
}
