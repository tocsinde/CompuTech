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
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManager;
import org.salespointframework.order.OrderStatus;

import java.math.BigDecimal;
import java.util.Optional;

import org.salespointframework.order.ProductPaymentEntry;
import org.salespointframework.quantity.Metric;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountIdentifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import computech.model.CustomerRepository;
import org.salespointframework.useraccount.UserAccountManager;

import computech.model.validation.customerEditForm;
import computech.model.validation.employeeEditForm;
import computech.model.validation.registerEmployeeForm;

import javax.management.relation.RoleStatus;
import javax.validation.Valid;

import computech.model.Customer;

@Controller
class BossController {
	private static final Quantity NONE = Quantity.of(0);
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

	@PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_BOSS')")
	@RequestMapping("/customers")
	public String customers(ModelMap modelMap) {

		modelMap.addAttribute("customerList", customerRepository.findAll());
		return "customers";
	}

  @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_BOSS')")
	@RequestMapping(value = "/customers/delete/{id}", method = RequestMethod.POST)
	public String removeCustomer(@PathVariable Long id) {
		customerRepository.delete(id);
		return "redirect:/customers";
	}

  @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_BOSS')")
	@RequestMapping(value = "/customers/edit/{id}")
	public String editCustomer(@PathVariable("id") Long id, Model model, ModelMap modelMap) {
		modelMap.addAttribute("customerEditForm", new customerEditForm());
		Customer customer_found = customerRepository.findOne(id);

		model.addAttribute("customer", customer_found);

		return "customers_edit";
	}

  @PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_BOSS')")
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

		/* if(customerEditForm.getPassword() != "") {
			userAccountManager.changePassword(customer_found.getUserAccount(), customerEditForm.getPassword());
		} */

		return "redirect:/customers";
	}

  @PreAuthorize("hasRole('ROLE_BOSS')")
	@RequestMapping("/employees")
	public String employees(ModelMap modelMap) {

		modelMap.addAttribute("employeeList_enabled", userAccountManager.findEnabled());
		modelMap.addAttribute("employeeList_disabled", userAccountManager.findDisabled());
		return "employees";
	}

	@PreAuthorize("hasRole('ROLE_BOSS')")
	@RequestMapping(value = "/employees/disable/{userAccountIdentifier}", method = RequestMethod.POST)
	public String disableEmployee(@PathVariable UserAccountIdentifier userAccountIdentifier) {
		userAccountManager.disable(userAccountIdentifier);
		return "redirect:/employees";
	}

	@PreAuthorize("hasRole('ROLE_BOSS')")
	@RequestMapping(value = "/employees/enable/{userAccountIdentifier}", method = RequestMethod.POST)
	public String enableEmployee(@PathVariable UserAccountIdentifier userAccountIdentifier) {
		userAccountManager.enable(userAccountIdentifier);
		return "redirect:/employees";
	}

	@PreAuthorize("hasRole('ROLE_BOSS')")
	@RequestMapping(value = "/employees/edit/{userAccountIdentifier}")
	public String editEmployee(@PathVariable UserAccountIdentifier userAccountIdentifier, Model model, ModelMap modelMap) {
		modelMap.addAttribute("employeeEditForm", new employeeEditForm());
		model.addAttribute("employee", userAccountIdentifier);

		return "employees_edit";
	}

	@PreAuthorize("hasRole('ROLE_BOSS')")
	@RequestMapping(value = "/employees/edit/{useraccount}", method = RequestMethod.POST)
	public String saveEmployee(@PathVariable UserAccount useraccount, Model model, @ModelAttribute("employeeEditForm") @Valid employeeEditForm employeeEditForm, BindingResult result) {

		if(employeeEditForm.getPassword() != "") {
			//UserAccount user_found = (User) userAccountManager.get(userAccountIdentifier);
			userAccountManager.changePassword(useraccount, employeeEditForm.getPassword());
		}

		return "redirect:/employees";
	}


	@PreAuthorize("hasRole('ROLE_BOSS')")
@RequestMapping("/registeremployee")
public String registerEmployee(ModelMap modelMap) {
	modelMap.addAttribute("registerEmployeeForm", new registerEmployeeForm());
	return "registerEmployee";
}

@PreAuthorize("hasRole('ROLE_BOSS')")
@RequestMapping(value="/registeremployee", method = RequestMethod.POST)
public String registerEmployee(@ModelAttribute("registerEmployeeForm") @Valid registerEmployeeForm registerEmployeeForm, BindingResult result) {
	if (result.hasErrors()) {
		return "registerEmployee";
	}

	UserAccount employee = userAccountManager.create(registerEmployeeForm.getNickname(), registerEmployeeForm.getPassword(), Role.of("ROLE_EMPLOYEE"));
	userAccountManager.save(employee);

	return "redirect:/employees";
}

	// Anfang Stockcontrolling
	@PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_BOSS')")
	@RequestMapping("/stock")
	public String stock(ModelMap modelMap) {

		modelMap.addAttribute("stock", inventory.findAll());

		return "stock";
	}
	@RequestMapping("/sdetail/{sid}")
	public String sdetail(@PathVariable("sid") Article article, ModelMap modelMap) {

		Optional<InventoryItem> item = inventory.findByProductIdentifier(article.getIdentifier());
		Quantity quantity = item.map(InventoryItem::getQuantity).orElse(NONE);

		modelMap.addAttribute("article", article);
		modelMap.addAttribute("quantity", quantity);



		return "sdetail";
	}
	@RequestMapping(value = "/addstock", method = RequestMethod.POST)
	public String addstock(@RequestParam("sid") Article article, @RequestParam("number1") int number, ModelMap modelMap) {
		Optional<InventoryItem> item = inventory.findByProductIdentifier(article.getIdentifier());
		Quantity quantity = item.map(InventoryItem::getQuantity).orElse(NONE);


		Quantity quantity2 = Quantity.of(number);



		InventoryItem i = item.get();
		i.increaseQuantity(quantity2);
		inventory.save(i);
		modelMap.addAttribute("stock", inventory.findAll());
		return "stock";
	}

	@RequestMapping(value = "/substock", method = RequestMethod.POST)
	public String substock(@RequestParam("sid") Article article, @RequestParam("number2") int number, ModelMap modelMap) {
		Optional<InventoryItem> item = inventory.findByProductIdentifier(article.getIdentifier());
		Quantity quantity = item.map(InventoryItem::getQuantity).orElse(NONE);
		BigDecimal amount1 = quantity.getAmount();
		int q = amount1.intValue();
		int amount = number;
		if (amount > q){
			amount =q;
		}
		Quantity quantity2 = Quantity.of(amount);



		InventoryItem i = item.get();
		i.decreaseQuantity(quantity2);
		inventory.save(i);
		modelMap.addAttribute("stock", inventory.findAll());
		return "stock";
	}
	@RequestMapping(value = "/stockdelete", method = RequestMethod.POST)
	public String stockdelete(@RequestParam("sid") Article article, ModelMap modelMap) {
		Optional<InventoryItem> item = inventory.findByProductIdentifier(article.getIdentifier());
		InventoryItem i = item.get();
		inventory.delete(i);

		return "stock";
	}

	//Ende Stockcontrolling
	@PreAuthorize("hasRole('ROLE_BOSS')")
	@RequestMapping("/balance")
	public String balance(ModelMap modelMap) {
		for (InventoryItem i :inventory.findAll()){
			Quantity q = i.getQuantity();
			Product p = i.getProduct();
			Money m = p.getPrice();
			BigDecimal bigd = q.getAmount();
			m.multiply(bigd);
			m.multiply(0.6);
			p.setPrice(m);
			//Hmm..noch ohne funktion
		}
		modelMap.addAttribute("stock", inventory.findAll());
		modelMap.addAttribute("ordersCompleted", orderManager.findBy(OrderStatus.COMPLETED));

		return "balance";
	}


	@PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_BOSS')")
	@RequestMapping("/orders")
	public String orders(ModelMap modelMap) {

	modelMap.addAttribute("ordersCompleted", orderManager.findBy(OrderStatus.COMPLETED));

	return "orders";
	}

	@PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_BOSS')")
	@RequestMapping("/sellorders")
	public String sell(ModelMap modelMap) {

	modelMap.addAttribute("sellCompleted",orderManager.findBy(OrderStatus.COMPLETED));

	return "sellorders";
	}

}
