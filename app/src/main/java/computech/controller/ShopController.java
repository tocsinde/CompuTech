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

	import computech.model.validation.profileEditForm;
	import org.salespointframework.useraccount.Role;
	import org.salespointframework.useraccount.UserAccount;
	import org.salespointframework.useraccount.UserAccountManager;
	import org.salespointframework.useraccount.web.LoggedIn;
	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.security.access.prepost.PreAuthorize;
	import org.springframework.stereotype.Controller;
	import org.springframework.ui.Model;
	import org.springframework.ui.ModelMap;
	import org.springframework.util.Assert;
	import org.springframework.validation.BindingResult;
	import org.springframework.web.bind.annotation.ModelAttribute;
	import org.springframework.web.bind.annotation.PathVariable;
	import org.springframework.web.bind.annotation.RequestMapping;

	import computech.model.Customer;
	import computech.model.CustomerRepository;
	import computech.model.validation.RegistrationForm;
	import org.springframework.web.bind.annotation.RequestMethod;

	import java.util.Optional;

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

		@RequestMapping("/login")
		public String login() {return "login";}

		@RequestMapping("/comp")
		public String aboutus() {return "compu";}

		@RequestMapping("/registerNew")
		public String registerNew(@ModelAttribute("registrationForm") @Valid RegistrationForm registrationForm, BindingResult result) {

			if (result.hasErrors()) {
				return "register";
			}

			// über das Registrierungsformular können
			// Ausnahme: als Chef kann man im modifizierten Formular die Rolle aussuchen

			UserAccount userAccount = userAccountManager.create(registrationForm.getNickname(), registrationForm.getPassword(), Role.of(registrationForm.getRole()));
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

		@PreAuthorize("hasAnyRole('ROLE_PCUSTOMER', 'ROLE_BCUSTOMER')")
		@RequestMapping("/profile")
		public String editProfile(Model model, ModelMap modelMap, @LoggedIn Optional<UserAccount> userAccount) {
			modelMap.addAttribute("profileEditForm", new profileEditForm());

			Customer customer = customerRepository.findByUserAccount(userAccount.get());

			model.addAttribute("customer", customer);
			return "profile";
		}

		@PreAuthorize("hasAnyRole('ROLE_PCUSTOMER', 'ROLE_BCUSTOMER')")
		@RequestMapping(value = "/profile", method = RequestMethod.POST)
		public String saveProfile(Model model, @ModelAttribute("profileEditForm") @Valid profileEditForm profileEditForm, BindingResult result, @LoggedIn Optional<UserAccount> userAccount) {
			Customer customer = customerRepository.findByUserAccount(userAccount.get());
			model.addAttribute("customer", customer);

			if (result.hasErrors()) {
				return "profile";
			}

			customer.setFirstname(profileEditForm.getFirstname());
			customer.setLastname(profileEditForm.getLastname());
			customer.setMail(profileEditForm.getMail());
			customer.setPhone(profileEditForm.getPhone());
			customer.setAddress(profileEditForm.getAddress());

			customerRepository.save(customer);

			if(profileEditForm.getPassword() != "") {
				userAccountManager.changePassword(customer.getUserAccount(), profileEditForm.getPassword());
			}

			return "profile";
		}


}

