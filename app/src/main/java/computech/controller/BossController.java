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
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManager;
import org.salespointframework.order.OrderStatus;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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

import org.salespointframework.useraccount.UserAccountManager;

import computech.model.validation.customerEditForm;
import computech.model.validation.employeeEditForm;
import computech.model.validation.registerEmployeeForm;
import computech.model.validation.addArticleForm;
import computech.model.validation.SellanwserForm;

import static org.salespointframework.core.Currencies.EURO;

import javax.management.relation.RoleStatus;
import javax.validation.Valid;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
class BossController {
	private static final Quantity NONE = Quantity.of(0);
	private final OrderManager<Order> orderManager;
	private final Inventory<InventoryItem> inventory;
	private final CustomerRepository customerRepository;
	private final UserAccountManager userAccountManager;
	private final SellRepository sellRepository;
	private final SellanwserRepository sellanwserRepository;


	@Autowired
	public BossController(OrderManager<Order> orderManager, Inventory<InventoryItem> inventory,
						  CustomerRepository customerRepository, UserAccountManager userAccountManager, SellRepository sellRepository, SellanwserRepository sellanwserRepository) {

		this.orderManager = orderManager;
		this.inventory = inventory;
		this.customerRepository = customerRepository;
		this.userAccountManager = userAccountManager;
		this.sellRepository = sellRepository;
		this.sellanwserRepository = sellanwserRepository;
	}

	/**
	 *
	 * @param modelMap
	 * @return
     */
	@PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_BOSS')")
	@RequestMapping("/customers")
	public String customers(ModelMap modelMap) {

		modelMap.addAttribute("customerList", customerRepository.findAll());
		return "customers";
	}


	/**
	 *
	 * @param id
	 * @return
     */
	@PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_BOSS')")
	@RequestMapping(value = "/customers/delete/{id}", method = RequestMethod.POST)
	public String removeCustomer(@PathVariable Long id) {
		customerRepository.delete(id);
		return "redirect:/customers";
	}


	/**
	 *
	 * @param id
	 * @param model
	 * @param modelMap
     * @return
     */
	@PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_BOSS')")
	@RequestMapping(value = "/customers/edit/{id}")
	public String editCustomer(@PathVariable("id") Long id, Model model, ModelMap modelMap) {
		modelMap.addAttribute("customerEditForm", new customerEditForm());
		Customer customer_found = customerRepository.findOne(id);

		model.addAttribute("customer", customer_found);
		modelMap.addAttribute("employeeList_enabled", userAccountManager.findEnabled());

		if(customer_found.getConnectedEmployee() != null) {
			model.addAttribute("currentEmployee", customer_found.getConnectedEmployee().getUsername());
		}

		return "customers_edit";
	}

	/**
	 *
	 * @param id
	 * @param model
	 * @param customerEditForm
	 * @param result
	 * @param modelMap
     * @return
     */
	@PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_BOSS')")
	@RequestMapping(value = "/customers/edit/{id}", method = RequestMethod.POST)
	public String saveCustomer(@PathVariable("id") Long id, Model model, @ModelAttribute("customerEditForm") @Valid customerEditForm customerEditForm, BindingResult result, ModelMap modelMap) {
		Customer customer_found = customerRepository.findOne(id);
		model.addAttribute("customer", customer_found);
		modelMap.addAttribute("employeeList_enabled", userAccountManager.findEnabled());

		if (result.hasErrors()) {
			return "customers_edit";
		}


		customer_found.setFirstname(customerEditForm.getFirstname());
		customer_found.setLastname(customerEditForm.getLastname());
		customer_found.setMail(customerEditForm.getMail());
		customer_found.setPhone(customerEditForm.getPhone());
		customer_found.setAddress(customerEditForm.getAddress());

		// Mitarbeiter-Änderung bei Geschäftskunden
		if (!customerEditForm.getConnectedEmployee().isEmpty()) {

			UserAccount employee = userAccountManager.findByUsername(customerEditForm.getConnectedEmployee()).get();
			customer_found.setConnectedEmployee(employee);
		}

		customerRepository.save(customer_found);

		/* if(customerEditForm.getPassword() != "") {
			userAccountManager.changePassword(customer_found.getUserAccount(), customerEditForm.getPassword());
		} */

		return "redirect:/customers";
	}

	/**
	 *
	 * @param modelMap
	 * @return
     */
	@PreAuthorize("hasRole('ROLE_BOSS')")
	@RequestMapping("/employees")
	public String employees(ModelMap modelMap) {

		modelMap.addAttribute("employeeList_enabled", userAccountManager.findEnabled());
		modelMap.addAttribute("employeeList_disabled", userAccountManager.findDisabled());
		return "employees";
	}

	/**
	 *
	 * @param userAccountIdentifier
	 * @return
     */
	@PreAuthorize("hasRole('ROLE_BOSS')")
	@RequestMapping(value = "/employees/disable/{userAccountIdentifier}", method = RequestMethod.POST)
	public String disableEmployee(@PathVariable UserAccountIdentifier userAccountIdentifier, RedirectAttributes success) {
		userAccountManager.disable(userAccountIdentifier);
		success.addFlashAttribute("success", "Der Mitarbeiter wurde erfolgreich deaktiviert.");

		return "redirect:/employees";
	}

	/**
	 *
	 * @param userAccountIdentifier
	 * @return
     */
	@PreAuthorize("hasRole('ROLE_BOSS')")
	@RequestMapping(value = "/employees/enable/{userAccountIdentifier}", method = RequestMethod.POST)
	public String enableEmployee(@PathVariable UserAccountIdentifier userAccountIdentifier, RedirectAttributes success) {
		userAccountManager.enable(userAccountIdentifier);
		success.addFlashAttribute("success", "Der Mitarbeiter wurde erfolgreich freigeschaltet.");

		return "redirect:/employees";
	}

	/**
	 *
	 * @param userAccountIdentifier
	 * @param model
	 * @param modelMap
     * @return
     */
	@PreAuthorize("hasRole('ROLE_BOSS')")
	@RequestMapping(value = "/employees/edit/{userAccountIdentifier}")
	public String editEmployee(@PathVariable UserAccountIdentifier userAccountIdentifier, Model model, ModelMap modelMap) {
		modelMap.addAttribute("employeeEditForm", new employeeEditForm());
		model.addAttribute("employee", userAccountIdentifier);

		return "employees_edit";
	}

	/**
	 *
	 * @param useraccount
	 * @param model
	 * @param employeeEditForm
	 * @param result
     * @return
     */
	@PreAuthorize("hasRole('ROLE_BOSS')")
	@RequestMapping(value = "/employees/edit/{useraccount}", method = RequestMethod.POST)
	public String saveEmployee(@PathVariable UserAccount useraccount, Model model, @ModelAttribute("employeeEditForm") @Valid employeeEditForm employeeEditForm, BindingResult result, RedirectAttributes success) {

		if(employeeEditForm.getPassword() != "") {
			//UserAccount user_found = (User) userAccountManager.get(userAccountIdentifier);
			userAccountManager.changePassword(useraccount, employeeEditForm.getPassword());
		}

		success.addFlashAttribute("success", "Der Mitarbeiter wurde erfolgreich bearbeitet.");

		return "redirect:/employees";
	}

	/**
	 *
	 * @param modelMap
	 * @return
     */
	@PreAuthorize("hasRole('ROLE_BOSS')")
	@RequestMapping("/registeremployee")
	public String registerEmployee(ModelMap modelMap) {
		modelMap.addAttribute("registerEmployeeForm", new registerEmployeeForm());
		return "registerEmployee";
	}

	/**
	 *
	 * @param registerEmployeeForm
	 * @param result
     * @return
     */
	@PreAuthorize("hasRole('ROLE_BOSS')")
	@RequestMapping(value="/registeremployee", method = RequestMethod.POST)
	public String registerEmployee(@ModelAttribute("registerEmployeeForm") @Valid registerEmployeeForm registerEmployeeForm, BindingResult result, RedirectAttributes success) {
		if (result.hasErrors()) {
			return "registerEmployee";
		}

		success.addFlashAttribute("success", "Der Mitarbeiter " + registerEmployeeForm.getNickname() + " wurde erfolgreich erstellt.");

		UserAccount employee = userAccountManager.create(registerEmployeeForm.getNickname(), registerEmployeeForm.getPassword(), Role.of("ROLE_EMPLOYEE"));
		userAccountManager.save(employee);


		return "redirect:/employees";
	}

	// Anfang Stockcontrolling

	/**
	 *
	 * @param modelMap
	 * @return
     */
	@PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_BOSS')")
	@RequestMapping("/stock")
	public String stock(ModelMap modelMap) {

		modelMap.addAttribute("stock", inventory.findAll());

		return "stock";
	}

	/**
	 *
	 * @param article
	 * @param modelMap
     * @return
     */
	@RequestMapping("/sdetail/{sid}")
	public String sdetail(@PathVariable("sid") Product article, ModelMap modelMap) {

		Optional<InventoryItem> item = inventory.findByProductIdentifier(article.getIdentifier());
		Quantity quantity = item.map(InventoryItem::getQuantity).orElse(NONE);

		modelMap.addAttribute("article", article);
		modelMap.addAttribute("quantity", quantity);



		return "sdetail";
	}



	/**
	 *
	 * @param article
	 * @param number
	 * @param modelMap
     * @return
     */
	@RequestMapping(value = "/addstock", method = RequestMethod.POST)
	public String addstock(@RequestParam("sid") Product article, @RequestParam("number1") int number, ModelMap modelMap) {
		Optional<InventoryItem> item = inventory.findByProductIdentifier(article.getIdentifier());
		Quantity quantity = item.map(InventoryItem::getQuantity).orElse(NONE);


		Quantity quantity2 = Quantity.of(number);



		InventoryItem i = item.get();
		i.increaseQuantity(quantity2);
		inventory.save(i);
		modelMap.addAttribute("stock", inventory.findAll());
		return "stock";
	}


	/**
	 *
	 * @param article
	 * @param number
	 * @param modelMap
     * @return
     */
	@RequestMapping(value = "/substock", method = RequestMethod.POST)
	public String substock(@RequestParam("sid") Product article, @RequestParam("number2") int number, ModelMap modelMap) {
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

	/**
	 *
	 * @param article
	 * @param modelMap
     * @return
     */
	@RequestMapping(value = "/stockdelete", method = RequestMethod.POST)
	public String stockdelete(@RequestParam("sid") Product article, ModelMap modelMap) {
		Optional<InventoryItem> item = inventory.findByProductIdentifier(article.getIdentifier());
		InventoryItem i = item.get();
		inventory.delete(i);
		modelMap.addAttribute("stock", inventory.findAll());
		return "stock";
	}

	//Ende Stockcontrolling

	/**
	 *
	 * @param modelMap
	 * @return
     */
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


	/**
	 *
	 * @param modelMap
	 * @return
     */
	@PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_BOSS')")
	@RequestMapping("/orders")
	public String orders(ModelMap modelMap) {

		modelMap.addAttribute("ordersCompleted", orderManager.findBy(OrderStatus.COMPLETED));

		return "orders";
	}


	/**
	 *
	 * @param modelMap
	 * @return
     */
	@PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_BOSS')")
	@RequestMapping("/sellorders")
	public String getSellorders(ModelMap modelmap) {

		modelmap.addAttribute("sellCompleted", sellRepository.findAll());

		return "sellorders";
	}
	
	@PreAuthorize("hayAnyRole('ROLE_EMPLOYEE', 'ROLE_BOSS')")
	@RequestMapping(value = "/sellorder/anwser/{id}")
	public String getsingleSellorder(@PathVariable("id") Long id, Model model, ModelMap modelmap) {
		
		modelmap.addAttribute("sellanwserForm", new SellanwserForm());
		SellOrder sellorder_found = sellRepository.findOne(id);
		Customer customer_of_sellorder = sellorder_found.getCustomer();
		model.addAttribute("customer", customer_of_sellorder);
		
		return "sellorder_anwser";
	}
	
	@PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_BOSS')")
	@RequestMapping(value = "/sellorder/anwser/{id}", method = RequestMethod.POST)
	public String sendSellanwser(@ModelAttribute("sellanwserForm") @Valid SellanwserForm sellanwserForm, @PathVariable("id") Long id, Model model, ModelMap modelmap, BindingResult result) {
		
		modelmap.addAttribute("sellanwserForm", new SellanwserForm());
		SellOrder sellorder_found = sellRepository.findOne(id);
		Customer customer_of_sellorder = sellorder_found.getCustomer();
		model.addAttribute("customer", customer_of_sellorder);
		
		
		sellanwserForm.setArticle(sellorder_found.getArticle());
		
		Sellanwser sellanwser = new Sellanwser(customer_of_sellorder, sellanwserForm.getArticle(), sellanwserForm.getAnwser());
		sellanwserRepository.save(sellanwser);
		
		return "redirect:/sellorders";
	}

	/**
	 *
	 * @param modelMap
	 * @return
     */
	@PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_BOSS')")
	@RequestMapping("/addarticle")
	public String addArticle(ModelMap modelMap) {
		modelMap.addAttribute("types", Article.ArticleType.values());
		modelMap.addAttribute("addArticleForm", new addArticleForm());
		return "addArticle";
	}

	/**
	 *
	 * @param modelMap
	 * @param addArticleForm
	 * @param result
     * @return
     */
	@PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_BOSS')")
	@RequestMapping(value ="/addarticle", method = RequestMethod.POST)
	public String addArticleToCatalog(ModelMap modelMap, @ModelAttribute("addArticleForm") @Valid addArticleForm addArticleForm, BindingResult result, @RequestParam("file") MultipartFile file) {
		modelMap.addAttribute("types", Article.ArticleType.values());

		if (result.hasErrors()) {
			return "addArticle";
		}

		try {
			byte[] bytes = file.getBytes();
			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File("src/main/resources/static/resources/img/cover/" + file.getOriginalFilename())));
			stream.write(bytes);
			stream.close();
		} catch (Exception e) {
			System.out.println("Upload failed:  => " + e.getMessage());
		}

		Article newarticle = new Article(addArticleForm.getName(), file.getOriginalFilename(), Money.of(addArticleForm.getPrice(), EURO), addArticleForm.getModel(), Article.ArticleType.valueOf(addArticleForm.getType()));
		InventoryItem newitem = new InventoryItem(newarticle, Quantity.of(addArticleForm.getQuantity()));
		inventory.save(newitem);

		return "redirect:/stock";
	}

}
