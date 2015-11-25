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


	import javax.validation.Valid;

	import org.salespointframework.useraccount.Role;
	import org.salespointframework.useraccount.UserAccount;
	import org.salespointframework.useraccount.UserAccountManager;
	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.stereotype.Controller;
	import org.springframework.ui.ModelMap;
	import org.springframework.util.Assert;
	import org.springframework.validation.BindingResult;
	import org.springframework.web.bind.annotation.ModelAttribute;
	import org.springframework.web.bind.annotation.RequestMapping;

	import computech.model.Customer;
	import computech.model.CustomerRepository;
	import computech.model.validation.RegistrationForm;

	@Controller
	class ShopController {

		private final UserAccountManager userAccountManager;
		private final CustomerRepository customerRepository;

		@Autowired
		public ShopController(UserAccountManager userAccountManager, CustomerRepository customerRepository) {

			Assert.notNull(userAccountManager, "UserAccountManager must not be null!");
			Assert.notNull(customerRepository, "CustomerRepository must not be null!");

			this.userAccountManager = userAccountManager;
			this.customerRepository = customerRepository;
		}

		@RequestMapping({ "/", "/index" })
		public String index() {
			return "index";
		}

		@RequestMapping("/comp")
		public String aboutus() {return "compu";}

		@RequestMapping("/registerNew")
		public String registerNew(@ModelAttribute("registrationForm") @Valid RegistrationForm registrationForm,
				BindingResult result) {

			if (result.hasErrors()) {
				return "register";
			}

			// todo:	festlegen, welche Art von Kunde gerade registriert wird (Privat- oder Geschäftskunde)
			// 			mögliche Lösung: Button auf Registrierungsformular, der festlegt, welche Art von Kunde vorliegt
			UserAccount userAccount = userAccountManager.create(registrationForm.getNickname(), registrationForm.getPassword(), Role.of("ROLE_PCUSTOMER"));
			userAccountManager.save(userAccount);

			Customer customer = new Customer(userAccount, registrationForm.getAddress(), registrationForm.getFirstname(), registrationForm.getLastname(), registrationForm.getMail(), registrationForm.getPhone());
			customerRepository.save(customer);

			return "redirect:/";
		}

		@RequestMapping("/register")
		public String register(ModelMap modelMap) {
			modelMap.addAttribute("registrationForm", new RegistrationForm());
			return "register";
		}
		
}

