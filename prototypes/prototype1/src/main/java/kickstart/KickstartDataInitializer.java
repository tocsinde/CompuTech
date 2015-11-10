package kickstart;

// ähm, wofür benötigen wir das (wenn überhaupt)?
import static org.salespointframework.core.Currencies.*;

import kickstart.model.Customer;
import kickstart.model.CustomerRepository;

import java.util.Arrays;

import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;

	@Override
	public void initialize() {
		initializeUsers(userAccountManager, customerRepository);
	}
	
	private void initializeUsers(UserAccountManager userAccountManager, CustomerRepository customerRepository) {
		
		if (userAccountManager.findByUsername("admin").isPresent()) {
			return;
		}

		UserAccount admin = userAccountManager.create("admin", "123", new Role("ROLE_BOSS"));
		userAccountManager.save(admin);
		
		admin  = userAccountManager.create("admin", "123", new Role("ROLE_BOSS"));
		userAccountManager.save(admin);
		
		
		employee  = userAccountManager.create("employee1", "123", new Role("ROLE_EMPLOYEE"));
		userAccountManager.save(employee);
	
		
		
		final Role customerRole = new Role("ROLE_BCUSTOMER"); 
		
		// hier wird erstmal allgemein ein Account auf Salespoint-Basis erstellt
		UserAccount ua1 = userAccountManager.create("haensel", "123", customerRole);
		userAccountManager.save(ua1);
		UserAccount ua2 = userAccountManager.create("gretel", "123", customerRole);
		userAccountManager.save(ua2);
		
		
		// hier werden zusätzliche Daten für die GESCHÄFTSKunden ergänzt
		Customer c1 = new Customer(ua1, "Straße 1", "Hänsel", "Nachname", "h@ensel.de", "0800-1234567");
		Customer c2 = new Customer(ua2, "Straße 2", "Gretel", "Nachname", "gretel@web.de", "0800-7891011");
		
		
		final Role customerRole = new Role("ROLE_PCUSTOMER"); 
		
		// hier wird erstmal allgemein ein Account auf Salespoint-Basis erstellt
		UserAccount ua3 = userAccountManager.create("haensel", "123", customerRole);
		userAccountManager.save(ua1);
		UserAccount ua4 = userAccountManager.create("gretel", "123", customerRole);
		userAccountManager.save(ua2);
		
		
		// hier werden zusätzliche Daten für die PRIVATKunden ergänzt
		Customer c3 = new Customer(ua3, "Straße 1", "Hänsel", "Nachname", "h@ensel.de", "0800-1234567");
		Customer c4 = new Customer(ua4, "Straße 2", "Gretel", "Nachname", "gretel@web.de", "0800-7891011");
		
		
		// alle Kunden (Geschäfts- und Privatkunden) speichern
		customerRepository.save(Arrays.asList(c1, c2, c3, c4));
	}