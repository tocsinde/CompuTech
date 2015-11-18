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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import kickstart.model.CustomerRepository;
import org.salespointframework.useraccount.UserAccountManager;

import kickstart.model.validation.customerEditForm;
import kickstart.model.validation.employeeEditForm;

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
	public String editCustomer(@PathVariable("id") Long id, Model model, ModelMap modelMap) {
		modelMap.addAttribute("customerEditForm", new customerEditForm());
		Customer customer_found = customerRepository.findOne(id);

		model.addAttribute("customer", customer_found);

		return "customers_edit";
	}

	@RequestMapping(value = "/customers/edit/{id}", method = RequestMethod.POST)
	public String saveCustomer(@PathVariable("id") Long id, Model model, @ModelAttribute("customerEditForm") @Valid customerEditForm customerEditForm, BindingResult result) {
		Customer customer_found = customerRepository.findOne(id);
		model.addAttribute("customer", customer_found);

		if (result.hasErrors()) {
			return "customers_edit";
		}


		customer_found.setFirstname(customerEditForm.getFirstname());
		customer_found.setLastname(customerEditForm.getLastname());
		customer_found.setMail(customerEditForm.getMail());
		customer_found.setPhone(customerEditForm.getPhone());
		customer_found.setAddress(customerEditForm.getAddress());

		customerRepository.save(customer_found);

		if(customerEditForm.getPassword() != "") {
			userAccountManager.changePassword(customer_found.getUserAccount(), customerEditForm.getPassword());
		}

		return "redirect:/customers_edit";
	}

	@RequestMapping("/employees")
	public String employees(ModelMap modelMap) {

		modelMap.addAttribute("employeeList_enabled", userAccountManager.findEnabled());
		modelMap.addAttribute("employeeList_disabled", userAccountManager.findDisabled());
		return "employees";
	}

	@RequestMapping(value = "/employees/disable/{userAccountIdentifier}", method = RequestMethod.POST)
	public String disableEmployee(@PathVariable UserAccountIdentifier userAccountIdentifier) {
		userAccountManager.disable(userAccountIdentifier);
		return "redirect:/employees";
	}

	@RequestMapping(value = "/employees/enable/{userAccountIdentifier}", method = RequestMethod.POST)
	public String enableEmployee(@PathVariable UserAccountIdentifier userAccountIdentifier) {
		userAccountManager.enable(userAccountIdentifier);
		return "redirect:/employees";
	}

	@RequestMapping(value = "/employees/edit/{userAccountIdentifier}")
	public String editEmployee(@PathVariable UserAccountIdentifier userAccountIdentifier, Model model, ModelMap modelMap) {
		modelMap.addAttribute("employeeEditForm", new employeeEditForm());
		model.addAttribute("employee", userAccountIdentifier);

		return "employees_edit";
	}

	@RequestMapping(value = "/employees/edit/{userAccountIdentifier}", method = RequestMethod.POST)
	public String saveEmployee(@PathVariable UserAccountIdentifier userAccountIdentifier, Model model, @ModelAttribute("employeeEditForm") @Valid employeeEditForm employeeEditForm, BindingResult result) {

		if(employeeEditForm.getPassword() != "") {
			// todo
			//userAccountManager.changePassword();
		}

		return "redirect:/employees";
	}
}
