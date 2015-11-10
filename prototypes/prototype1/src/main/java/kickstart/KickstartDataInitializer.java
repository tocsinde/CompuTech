package kickstart;

import java.util.Arrays;
import static org.joda.money.CurrencyUnit.*;

import kickstart.model.Computer;
import kickstart.model.Computer.ComputerType;
import kickstart.model.ComputerCatalog;
import kickstart.model.Customer;
import kickstart.model.CustomerRepository;
import org.salespointframework.quantity.Units;

import org.javamoney.moneta.Money;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


@Component
public class KickstartDataInitializer implements DataInitializer {

	private final Inventory<InventoryItem> inventory;
	private final ComputerCatalog computerCatalog;
	private final UserAccountManager userAccountManager;
	private final CustomerRepository customerRepository;

	@Autowired
	public KickstartDataInitializer(CustomerRepository customerRepository, Inventory<InventoryItem> inventory,
			UserAccountManager userAccountManager, ComputerCatalog computerCatalog) {

		Assert.notNull(customerRepository, "CustomerRepository must not be null!");
		Assert.notNull(inventory, "Inventory must not be null!");
		Assert.notNull(userAccountManager, "UserAccountManager must not be null!");
		Assert.notNull(computerCatalog, "ComputerCatalog must not be null!");

		this.customerRepository = customerRepository;
		this.inventory = inventory;
		this.userAccountManager = userAccountManager;
		this.computerCatalog = computerCatalog;
	}
	@Override
	public void initialize() {
		initializeUsers(userAccountManager, customerRepository);
		initializeCatalog(computerCatalog, inventory);
	}
	
	private void initializeCatalog(ComputerCatalog computerCatalog, Inventory<InventoryItem> inventory) {

		if (computerCatalog.findAll().iterator().hasNext()) {
			return;
		}

		computerCatalog.save(new Computer("Samsung", "sam1", Money.of(EUR, 199.99), "a1", ComputerType.NOTEBOOK));
		computerCatalog.save(new Computer("Samsung", "sam2", Money.of(EUR, 299.99), "a2", ComputerType.NOTEBOOK));
		
		computerCatalog.save(new Computer("Acer", "ace1", Money.of(EUR, 299.99), "b1", ComputerType.COMPUTER));
		computerCatalog.save(new Computer("Acer", "ace2", Money.of(EUR, 299.99), "b2", ComputerType.COMPUTER));
	
		//  soll 10 Stück jeweils verfügbar sein
		
				for (Computer comp : ComputerCatalog.findAll()) {
					InventoryItem inventoryItem = new InventoryItem(comp, Units.TEN);
					inventory.save(inventoryItem);
				}
	}
		
	
private void initializeUsers(UserAccountManager userAccountManager, CustomerRepository customerRepository) {
		
		if (userAccountManager.findByUsername("admin").isPresent()) {
			return;
		}

		UserAccount admin = userAccountManager.create("admin", "123", new Role("ROLE_BOSS"));
		userAccountManager.save(admin);
		
		admin  = userAccountManager.create("admin", "123", new Role("ROLE_BOSS"));
		userAccountManager.save(admin);
		

		UserAccount employee  = userAccountManager.create("employee1", "123", new Role("ROLE_EMPLOYEE"));
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
		
		
		final Role customerRole2 = new Role("ROLE_PCUSTOMER"); 
		
		// hier wird erstmal allgemein ein Account auf Salespoint-Basis erstellt
		UserAccount ua3 = userAccountManager.create("haensel", "123", customerRole2);
		userAccountManager.save(ua1);
		UserAccount ua4 = userAccountManager.create("gretel", "123", customerRole2);
		userAccountManager.save(ua2);
		
		
		// hier werden zusätzliche Daten für die PRIVATKunden ergänzt
		Customer c3 = new Customer(ua3, "Straße 1", "Hänsel", "Nachname", "h@ensel.de", "0800-1234567");
		Customer c4 = new Customer(ua4, "Straße 2", "Gretel", "Nachname", "gretel@web.de", "0800-7891011");
		
		
		// alle Kunden (Geschäfts- und Privatkunden) speichern
		customerRepository.save(Arrays.asList(c1, c2, c3, c4));
	}
}