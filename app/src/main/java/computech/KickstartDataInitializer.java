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

package computech;

import computech.model.*;
import computech.model.Article.ArticleType;

import org.javamoney.moneta.Money;
import org.salespointframework.core.DataInitializer;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Arrays;

import static org.salespointframework.core.Currencies.EURO;


@Component
public class KickstartDataInitializer implements DataInitializer {

	private final Inventory<InventoryItem> inventory;
	private final ComputerCatalog computerCatalog;
	private final UserAccountManager userAccountManager;
	private final CustomerRepository customerRepository;
	private final RepairRepository repairRepository;
	private final SellRepository sellRepository;

	@Autowired
	public KickstartDataInitializer(CustomerRepository customerRepository, Inventory<InventoryItem> inventory,
			UserAccountManager userAccountManager, ComputerCatalog computerCatalog, RepairRepository repairRepository, SellRepository sellRepository) {

		Assert.notNull(customerRepository, "CustomerRepository must not be null!");
		Assert.notNull(inventory, "Inventory must not be null!");
		Assert.notNull(userAccountManager, "UserAccountManager must not be null!");
		Assert.notNull(computerCatalog, "ComputerCatalog must not be null!");
		Assert.notNull(repairRepository, "ComputerCatalog must not be null!");
		Assert.notNull(sellRepository, "sellRepository must not be null!");

		this.customerRepository = customerRepository;
		this.inventory = inventory;
		this.userAccountManager = userAccountManager;
		this.computerCatalog = computerCatalog;
		this.repairRepository = repairRepository;
		this.sellRepository = sellRepository;
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

		computerCatalog.save(new Article("Samsung1", "sam1", Money.of(199.99, EURO), "a1", Article.ArticleType.NOTEBOOK));
		computerCatalog.save(new Article("Samsung2", "sam2", Money.of(299.99, EURO), "a2", Article.ArticleType.NOTEBOOK));

		computerCatalog.save(new Article("Acer1", "ace1", Money.of(999.99, EURO), "b1", Article.ArticleType.COMPUTER));
		computerCatalog.save(new Article("Acer2", "ace2", Money.of(799.99, EURO), "b2", Article.ArticleType.COMPUTER));

		computerCatalog.save(new Article("Kaspersky Lab 2016", "kasp", Money.of(49.99, EURO), "c1", Article.ArticleType.SOFTWARE));
		computerCatalog.save(new Article("Avira Antivir pro", "avi", Money.of(29.99, EURO), "c2", Article.ArticleType.SOFTWARE));

		computerCatalog.save(new Article("USB 3.0-Kabel", "us3", Money.of(9.99, EURO), "d1", Article.ArticleType.ZUBE));
		computerCatalog.save(new Article("USB 2.0-Kabel", "us2", Money.of(4.99, EURO), "d2", Article.ArticleType.ZUBE));

		//  soll jeweils 10 Mal verfügbar sein

		for (Article comp : computerCatalog.findAll()) {
			InventoryItem inventoryItem = new InventoryItem(comp, Quantity.of(10));
			inventory.save(inventoryItem);
		}
	}


	private void initializeUsers(UserAccountManager userAccountManager, CustomerRepository customerRepository) {

		if (userAccountManager.findByUsername("boss").isPresent()) {
			return;
		}

		UserAccount admin = userAccountManager.create("boss", "123", Role.of("ROLE_BOSS"));
		userAccountManager.save(admin);


		UserAccount employee = userAccountManager.create("employee1", "123", Role.of("ROLE_EMPLOYEE"));
		userAccountManager.save(employee);


		final Role customerRole = Role.of("ROLE_BCUSTOMER");

		// hier wird erstmal allgemein ein Account auf Salespoint-Basis erstellt
		UserAccount ua1 = userAccountManager.create("Bhaensel", "123", customerRole);
		userAccountManager.save(ua1);
		UserAccount ua2 = userAccountManager.create("Bgretel", "123", customerRole);
		userAccountManager.save(ua2);


		// hier werden zusätzliche Daten für die GESCHÄFTSKunden ergänzt
		Customer c1 = new Customer(ua1, "Straße 1", "Hänsel", "Nachname", "h@ensel.de", "0800-1234567", null);
		Customer c2 = new Customer(ua2, "Straße 2", "Gretel", "Nachname", "gretel@web.de", "0800-7891011", null);


		final Role customerRole2 = Role.of("ROLE_PCUSTOMER");

		// hier wird erstmal allgemein ein Account auf Salespoint-Basis erstellt
		UserAccount ua3 = userAccountManager.create("Phaensel", "123", customerRole2);
		userAccountManager.save(ua3);
		UserAccount ua4 = userAccountManager.create("Pgretel", "123", customerRole2);
		userAccountManager.save(ua4);


		// hier werden zusätzliche Daten für die PRIVATKunden ergänzt
		Customer c3 = new Customer(ua3, "Straße 1", "Hänsel", "Nachname", "h@ensel.de", "0800-1234567",null);
		Customer c4 = new Customer(ua4, "Straße 2", "Gretel", "Nachname", "gretel@web.de", "0800-7891011",null);


		// alle Kunden (Geschäfts- und Privatkunden) speichern
		customerRepository.save(Arrays.asList(c1, c2, c3, c4));
	}

	private void initializeReparation(RepairRepository repairRepository) {


		if (repairRepository.findAll().iterator().hasNext()) {
			return;
		}
		Iterable<Customer> allCustomers = customerRepository.findAll();
		Iterable<Article> allArticles = computerCatalog.findAll();
		if (allCustomers.iterator().hasNext()) {
			if (allArticles.iterator().hasNext()) {
				Reparation rep = new Reparation(allCustomers.iterator().next(), allArticles.iterator().next(), "gute Zustand");
				repairRepository.save(rep);
			}
			computerCatalog.findAll();

		}
	}
	
	private void initializeSell(SellRepository sellRepository) {


		if (sellRepository.findAll().iterator().hasNext()) {
			return;
		}
		Iterable<Customer> allCustomers = customerRepository.findAll();
		Iterable<Article> allArticles = computerCatalog.findAll();
		//Iterable<Model> allModels = modelCatalog.findAll();
		if (allCustomers.iterator().hasNext()) {
			if (allArticles.iterator().hasNext()) {
				SellOrder sellorder = new SellOrder(allCustomers.iterator().next(), ArticleType.NOTEBOOK, allArticles.iterator().next(), "guter Zustand");
				sellRepository.save(sellorder);
			}
			sellRepository.findAll();

		}
	}
}