package kickstart.controller;

import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManager;
import org.salespointframework.order.OrderStatus;

import java.util.Optional;

import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountIdentifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import kickstart.model.CustomerRepository;
import org.salespointframework.useraccount.UserAccountManager;

import kickstart.model.validation.RegistrationForm;

import javax.management.relation.RoleStatus;
import javax.validation.Valid;

import kickstart.model.Customer;

@Controller
@PreAuthorize("hasRole('ROLE_BOSS')")
class BossController {

	private final OrderManager<Order> orderManager;
	private final Inventory<InventoryItem> inventory;
	private final CustomerRepository customerRepository;
	private final UserAccountManager userAccountManager;

	@Autowired
	public BossController(OrderManager<Order> orderManager, Inventory<InventoryItem> inventory,
			CustomerRepository customerRepository, UserAccountManager userAccountManager) {

		this.orderManager = orderManager;
		this.inventory = inventory;
		this.customerRepository = customerRepository;
		this.userAccountManager = userAccountManager;
	}

	@RequestMapping("/customers")
	public String customers(ModelMap modelMap) {

		modelMap.addAttribute("customerList", customerRepository.findAll());
		return "customers";
	}

	@RequestMapping(value = "/customers/delete/{id}", method = RequestMethod.POST)
	public String removeCustomer(@PathVariable Long id) {
		customerRepository.delete(id);
		return "redirect:/customers";
	}

	@RequestMapping(value = "/customers/edit/{id}")
	public String editCustomer(@PathVariable("id") Long id, Model model) {
		Customer customer_found = customerRepository.findOne(id);

		model.addAttribute("customer", customer_found);

		return "customers_edit";
	}

	@RequestMapping(value = "/customers/edit/{id}", method = RequestMethod.POST)
	public String saveCustomer(@PathVariable("id") Long id, @ModelAttribute("registrationForm") @Valid RegistrationForm registrationForm) {
		Customer customer_found = customerRepository.findOne(id);


		// THIS IS USER-ACCOUNT-STUFF, hard to realize
		// change password has extra function
		// TODO: change username

		//userAccountManager.changePassword(customer_found, "NEWPASSWORD");

		//UserAccount userAccount = userAccountManager.create(customer_found, registrationForm.getPassword(), Role.of("ROLE_PCUSTOMER"));




		return "redirect:/customers";
	}

	@RequestMapping("/employees")
	public String employees(ModelMap modelMap) {

		modelMap.addAttribute("employeeList", userAccountManager.findAll());
		return "employees";
	}

	@RequestMapping(value = "/employees/disable/{userAccountIdentifier}", method = RequestMethod.POST)
	public String disableEmployee(@PathVariable UserAccountIdentifier userAccountIdentifier) {
		userAccountManager.disable(userAccountIdentifier);
		return "redirect:/employees";
	}
}
