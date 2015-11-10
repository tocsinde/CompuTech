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
		
		
		// hier müssten noch Mitarbeiter rein
		// zu klären: welche ROLE wird denen zugeordnet? Kevin: bitte ROLE_EMPLOYEE verwenden
		// Recherche-Aufgabe: Wie werden Mitarbeiter in Salespoint festgelegt? Kevin: würde ich auch mit dem User machen
		// sind ja Mittelding zwischen Kunden und Admin
		
		
		UserAccount  = userAccountManager.create("admin", "123", new Role("ROLE_BOSS"));
		userAccountManager.save(admin);
		
		// Achja: hier muss noch zwischen Privat- und Geschäftskunden unterschieden werden
		// extra ROLE namens ROLE_NORMALCUSTOMER? // ROLE_PCUSTOMER
		// extra ROLE namens ROLE_ENTERPRISECUSTOMER? ROLE_BCUSTOMER (buissiness)
		
		
		final Role customerRole = new Role("ROLE_PCUSTOMER"); 
		
		// hier wird erstmal allgemein ein Account auf Salespoint-Basis erstellt
		UserAccount ua1 = userAccountManager.create("haensel", "123", customerRole);
		userAccountManager.save(ua1);
		UserAccount ua2 = userAccountManager.create("gretel", "123", customerRole);
		userAccountManager.save(ua2);
		
		
		
		
		// hier werden zusätzliche Daten für die Kunden ergänzt
		Customer c1 = new Customer(ua1, "Straße 1", "Hänsel", "Nachname", "h@ensel.de", "0800-1234567");
		Customer c2 = new Customer(ua2, "Straße 2", "Gretel", "Nachname", "gretel@web.de", "0800-7891011");
		
		customerRepository.save(Arrays.asList(c1, c2));
	}