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
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManager;
import org.salespointframework.order.OrderStatus;

import java.io.*;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
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
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 *
 * The BossController contains most of the administrative functions, such as the management of customers, employees and articles.
 *
 */

@Controller
public class BossController {
	private static final Quantity NONE = Quantity.of(0);
	private final OrderManager<Order> orderManager;
	private final Inventory<InventoryItem> inventory;
	private final CustomerRepository customerRepository;
	private final UserAccountManager userAccountManager;
	private final SellRepository sellRepository;
	private final SellanwserRepository sellanwserRepository;
	private final Inventory<InventoryItem> partsinventory;
	private final RepairRepository repairRepository;


	@Autowired
	public BossController(OrderManager<Order> orderManager, Inventory<InventoryItem> inventory,
						  CustomerRepository customerRepository, UserAccountManager userAccountManager, SellRepository sellRepository, SellanwserRepository sellanwserRepository, Inventory<InventoryItem> partsinventory, RepairRepository repairRepository) {

		this.orderManager = orderManager;
		this.inventory = inventory;
		this.customerRepository = customerRepository;
		this.userAccountManager = userAccountManager;
		this.sellRepository = sellRepository;
		this.sellanwserRepository = sellanwserRepository;
		this.partsinventory = partsinventory;
		this.repairRepository = repairRepository;
	}

	/**
	 *
	 * Responsible for the handling of "http://localhost:8080/productimg/image.xxx"-like requests.
	 *
	 * @param response delivers the requested file to the browser
	 * @param filename requested file name
	 * @param filetype filetype of the requested file
	 * @throws IOException if no image can be found in src/main/resources/static/resources/img/cover/
	 */
	@RequestMapping("/productimg/{file}.{filetype}")
	public void productimg(HttpServletResponse response, @PathVariable("file") String filename, @PathVariable("filetype") String filetype) throws IOException {
		BufferedInputStream image = new BufferedInputStream(new FileInputStream(new File("src/main/resources/static/resources/img/cover/" + filename + "." + filetype)));
		IOUtils.copy(image, response.getOutputStream());
	}

	/**
	 *
	 * Shows a list of all available customers.
	 *
	 * @param modelMap content of the customerRepository
	 * @return template "customers"
	 */
	@PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_BOSS')")
	@RequestMapping("/customers")
	public String customers(ModelMap modelMap) {

		modelMap.addAttribute("customerList", customerRepository.findAll());
		return "customers";
	}


	/**
	 * Deletes a customer.
	 *
	 * @param id ID of the customer who will be deleted
	 * @param success notification about deleting the selected customer
	 * @return redirect to template "customers"
	 */
	@PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_BOSS')")
	@RequestMapping(value = "/customers/delete/{id}", method = RequestMethod.POST)
	public String removeCustomer(@PathVariable Long id, RedirectAttributes success) {
		
		for (Reparation repair : repairRepository.findAll()) {
			if (repair.getCustomer() == customerRepository.findOne(id))
				repairRepository.delete(repair.getId());
		}
		customerRepository.delete(id);
		success.addFlashAttribute("success", "Der Kunde wurde erfolgreich gelöscht.");
		return "redirect:/customers";
	}


	/**
	 *
	 * Shows form for editing customer's data.
	 *
	 * @param id ID of the customer whose data will be edited
	 * @param model contains the attributes "customer" (selected customer) and "currentEmployee" (connected employee; for business customer)
	 * @param modelMap contains a list of enabled employees (for business customer) and the customerEditForm (for validation)
	 * @return template "customers_edit"
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
	 * Checks customer edit form and saves updated customer data.
	 *
	 * @param id ID of the customer whose data will be edited
	 * @param model contains the attribute "customer" (selected customer)
	 * @param customerEditForm form that needs to be validated
	 * @param result validation of the form
	 * @param modelMap contains a list of enabled employees (for business customer)
	 * @param success notification about editing the selected customer
	 * @return redirect to template "customers" (or back to the form when there are input errors to fix)
	 */
	@PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_BOSS')")
	@RequestMapping(value = "/customers/edit/{id}", method = RequestMethod.POST)
	public String saveCustomer(@PathVariable("id") Long id, Model model, @ModelAttribute("customerEditForm") @Valid customerEditForm customerEditForm, BindingResult result, ModelMap modelMap, RedirectAttributes success) {
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

		success.addFlashAttribute("success", "Der Kunde wurde erfolgreich bearbeitet.");

		return "redirect:/customers";
	}

	/**
	 *
	 * Shows a list of all available employees.
	 *
	 * @param modelMap contains a list of disabled and enabled employees
	 * @return template "employees"
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
	 * Disables an employee.
	 *
	 * @param userAccountIdentifier ID of the employee who will be disabled
	 * @param success notification about disabling the selected employee
	 * @return redirect to template "employees"
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
	 * Enables an employee.
	 *
	 * @param userAccountIdentifier ID of the employee who will be enabled
	 * @param success notification about enabling the selected employee
	 * @return redirect to template "employees"
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
	 * Shows form for editing employee's data.
	 *
	 * @param userAccountIdentifier nickname of the employee whose data will be edited
	 * @param model contains the attribute "employee" (selected employee)
	 * @param modelMap contains the customerEditForm (for validation)
	 * @return template "employees_edit"
	 */
	@PreAuthorize("hasRole('ROLE_BOSS')")
	@RequestMapping(value = "/employees/edit/{userAccountIdentifier}")
	public String editEmployee(@PathVariable UserAccountIdentifier userAccountIdentifier, Model model, ModelMap modelMap) {
		modelMap.addAttribute("employeeEditForm", new employeeEditForm());
		model.addAttribute("employee", userAccountIdentifier);

		return "employees_edit";
	}

	/**
	 * Checks employee edit form and saves updated employee data.
	 *
	 * @param useraccount UserAccount of the employee whose data will be edited
	 * @param employeeEditForm form that needs to be validated
	 * @param result validation of the form
	 * @param success notification about editing the selected employee
	 * @return redirect to template "employees"
	 */
	@PreAuthorize("hasRole('ROLE_BOSS')")
	@RequestMapping(value = "/employees/edit/{useraccount}", method = RequestMethod.POST)
	public String saveEmployee(@PathVariable UserAccount useraccount, @ModelAttribute("employeeEditForm") @Valid employeeEditForm employeeEditForm, BindingResult result, RedirectAttributes success) {

		if(employeeEditForm.getPassword() != "") {
			//UserAccount user_found = (User) userAccountManager.get(userAccountIdentifier);
			userAccountManager.changePassword(useraccount, employeeEditForm.getPassword());
		}

		success.addFlashAttribute("success", "Der Mitarbeiter wurde erfolgreich bearbeitet.");

		return "redirect:/employees";
	}

	/**
	 *
	 * Shows form for creating a new employee.
	 *
	 * @param modelMap form that needs to be validated
	 * @return template "registerEmployee"
	 */
	@PreAuthorize("hasRole('ROLE_BOSS')")
	@RequestMapping("/registeremployee")
	public String registerEmployee(ModelMap modelMap) {
		modelMap.addAttribute("registerEmployeeForm", new registerEmployeeForm());
		return "registerEmployee";
	}

	/**
	 *
	 * Checks employee register form and saves the new employee.
	 *
	 * @param registerEmployeeForm form that needs to be validated
	 * @param result validation of the form
	 * @param success notification about creating the new employee
	 * @return redirect to template "employees" (or back to the form when there are input errors to fix)
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
	 * Shows a list of all available products.
	 *
	 * @param modelMap contains a list of the products
	 * @return template "stock"
	 */
	@PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_BOSS')")
	@RequestMapping("/stock")
	public String stock(ModelMap modelMap) {

		modelMap.addAttribute("stock", inventory.findAll());

		return "stock";
	}

	/**
	 *
	 * Edit form for a single article.
	 *
	 * @param article requested article
	 * @param modelMap contains the article and it's quantity
	 * @return template "sdetail"
	 */
	@PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_BOSS')")
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
	 * Adds specific amount of an article to the stock.
	 *
	 * @param article requested article
	 * @param number number for raising the amount
	 * @param modelMap contains a list of the products
	 * @return template "stock"
	 */
	@PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_BOSS')")
	@RequestMapping(value = "/addstock", method = RequestMethod.POST)
	public String addstock(@RequestParam("sid") Product article, @RequestParam("number1")@Min(0) int number, ModelMap modelMap) {
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
	 * Removes specific amount from an article of the stock.
	 *
	 * @param article requested article
	 * @param number number for lowering the amount
	 * @param modelMap contains a list of the products
	 * @return template "stock"
	 */
	@PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_BOSS')")
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

	//Ende Stockcontrolling

	/**
	 *
	 * Shows income and expenses.
	 *
	 * @param modelMap contains the stock and completed orders
	 * @return template "balance"
	 */
	@PreAuthorize("hasRole('ROLE_BOSS')")
	@RequestMapping("/balance")
	public String balance(ModelMap modelMap) {
		List<Money> money = new LinkedList<Money>();
		for (InventoryItem i :inventory.findAll()){
			Quantity q = i.getQuantity();
			Product p = i.getProduct();
			Money m = p.getPrice();
			BigDecimal bigd = q.getAmount();
			Money f =m.multiply(bigd);
			Money g =f.multiply(0.6);
			money.add(g);
			//Hmm..noch ohne funktion
		}
		modelMap.addAttribute("money", money);
		modelMap.addAttribute("stock", inventory.findAll());
		modelMap.addAttribute("ordersCompleted", orderManager.findBy(OrderStatus.COMPLETED));

		return "balance";
	}


	/**
	 *
	 * Shows a list of the orders.
	 *
	 * @param modelMap contains completed orders
	 * @return template "orders"
	 */
	@PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_BOSS')")
	@RequestMapping("/orders")
	public String orders(ModelMap modelMap) {

		modelMap.addAttribute("ordersCompleted", orderManager.findBy(OrderStatus.COMPLETED));

		return "orders";
	}


	/**
	 * Shows sold products (re-bought from private customers).
	 *
	 * @param modelmap contains completed sells
	 * @return template "sellorders"
	 */
	@PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_BOSS')")
	@RequestMapping("/sellorders")
	public String getSellorders(ModelMap modelmap) {

		modelmap.addAttribute("sellCompleted", sellRepository.findAll());

		return "sellorders";
	}

	/**
	 *
	 * For answering sell requests of a private customer.
	 *
	 * @param id ID of the request
	 * @param model contains the customer who sent the request
	 * @param modelmap contains the sellanwserForm (for validation)
	 * @return template "sellorder_anwser"
	 */
	@PreAuthorize("hayAnyRole('ROLE_EMPLOYEE', 'ROLE_BOSS')")
	@RequestMapping(value = "/sellorder/anwser/{id}")
	public String getsingleSellorder(@PathVariable("id") Long id, Model model, ModelMap modelmap) {

		modelmap.addAttribute("sellanwserForm", new SellanwserForm());
		SellOrder sellorder_found = sellRepository.findOne(id);
		Customer customer_of_sellorder = sellorder_found.getCustomer();
		model.addAttribute("customer", customer_of_sellorder);

		return "sellorder_anwser";
	}

	/**
	 *
	 * For sending an answer to a sell request.
	 *
	 * @param sellanwserForm form that needs to be validated
	 * @param id ID of the request
	 * @param model contains the customer who sent the request
	 * @param modelmap contains the sellanswerForm, the answer and the price offer
	 * @param result validation of the form
	 * @return redirect to template "sellorders"
	 */
	@PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_BOSS')")
	@RequestMapping(value = "/sellorder/anwser/{id}", method = RequestMethod.POST)
	public String sendSellanwser(@ModelAttribute("sellanwserForm") @Valid SellanwserForm sellanwserForm, @PathVariable("id") Long id, Model model, ModelMap modelmap, BindingResult result) {

		modelmap.addAttribute("sellanwserForm", new SellanwserForm());
		SellOrder sellorder_found = sellRepository.findOne(id);
		Customer customer_of_sellorder = sellorder_found.getCustomer();
		model.addAttribute("customer", customer_of_sellorder);


		sellanwserForm.setArticle(sellorder_found.getArticle());
		modelmap.addAttribute("anwser", "anwser");
		modelmap.addAttribute("priceoffer", 16.20);


		Sellanwser sellanwser = new Sellanwser(customer_of_sellorder, sellanwserForm.getArticle(), sellanwserForm.getAnwser(), sellanwserForm.getPriceoffer());
		sellanwserRepository.save(sellanwser);

		return "redirect:/sellorders";
	}

	/**
	 *
	 * Form for adding an article.
	 *
	 * @param modelMap contains all available types for new articles and the concrete form for adding the article
	 * @return template "addArticle"
	 */
	@PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_BOSS')")
	@RequestMapping("/addarticle")
	public String addArticle(ModelMap modelMap) {
		modelMap.addAttribute("types", Article.ArticleType.values());
		modelMap.addAttribute("types2", Part.PartType.values());
		modelMap.addAttribute("types3", Computer.Computertype.values());
		modelMap.addAttribute("addArticleForm", new addArticleForm());
		return "addArticle";
	}

	/**
	 *
	 * Validates the article add form and adds an article.
	 *
	 * @param modelMap contains all available types for new articles
	 * @param addArticleForm form that needs to be validated
	 * @param result validation of the form
	 * @param file contains article image
	 * @param success notification about adding the article
	 * @return redirect to template "stock" (or back to the form when there are input errors to fix)
	 */
	@PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_BOSS')")
	@RequestMapping(value ="/addarticle", method = RequestMethod.POST)
	public String addArticleToCatalog(ModelMap modelMap, @ModelAttribute("addArticleForm") @Valid addArticleForm addArticleForm, BindingResult result, @RequestParam("file") MultipartFile file, RedirectAttributes success) {
		modelMap.addAttribute("types", Article.ArticleType.values());
		modelMap.addAttribute("types2", Part.PartType.values());
		modelMap.addAttribute("types3", Computer.Computertype.values());

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

		if(addArticleForm.getType().equals("ZUBE") || addArticleForm.getType().equals("SOFTWARE") || addArticleForm.getType().equals("NOTEBOOK") ) {

			Article newarticle = new Article(addArticleForm.getName(), file.getOriginalFilename(), Money.of(addArticleForm.getPrice(), EURO), addArticleForm.getModel(), Article.ArticleType.valueOf(addArticleForm.getType()));
			InventoryItem newitem = new InventoryItem(newarticle, Quantity.of(addArticleForm.getQuantity()));
			inventory.save(newitem);
		}

		if(addArticleForm.getType().equals("COMPUTER")) {
			Computer newcomputer = new Computer(addArticleForm.getName(), file.getOriginalFilename(), Money.of(addArticleForm.getPrice(), EURO), addArticleForm.getModel(), Computer.Computertype.COMPUTER);
			InventoryItem newitem = new InventoryItem(newcomputer, Quantity.of(addArticleForm.getQuantity()));
			inventory.save(newitem);
		}

		if(addArticleForm.getType().equals("PROCESSOR") || addArticleForm.getType().equals("RAM") || addArticleForm.getType().equals("GRAPHC") || addArticleForm.getType().equals("HARDD") ) {
			Part newpart = new Part(addArticleForm.getName(), file.getOriginalFilename(), Money.of(addArticleForm.getPrice(), EURO), addArticleForm.getModel(), Part.PartType.valueOf(addArticleForm.getType()));
			InventoryItem newitem = new InventoryItem(newpart, Quantity.of(addArticleForm.getQuantity()));
			partsinventory.save(newitem);
		}




		success.addFlashAttribute("success", "Der Artikel wurde erfolgreich hinzugefügt.");


		return "redirect:/stock";
	}

}
