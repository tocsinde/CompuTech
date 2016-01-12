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
import computech.model.Article.ArticleType;
import org.javamoney.moneta.Money;
import org.salespointframework.catalog.Product;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.time.BusinessTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.money.CurrencyUnit;
import java.math.BigDecimal;
import java.util.Optional;

/**
 *
 * The CatalogController provides functions for viewing article categories and single products as well.
 *
 */

@Controller
public class CatalogController {

	private static final Quantity NONE = Quantity.of(0);
	private final ComputerCatalog computerCatalog;
	private final PartsCatalog partsCatalog;
	private final AllinoneCatalog allinoneCatalog;
	private final Inventory<InventoryItem> partsinventory;
	private final Inventory<InventoryItem> inventory;
	private final BusinessTime businessTime;
	private final MessageSourceAccessor messageSourceAccessor;

	@Autowired
	public CatalogController(ComputerCatalog computerCatalog, Inventory<InventoryItem> inventory, BusinessTime businessTime, Inventory<InventoryItem> partsinventory, PartsCatalog partsCatalog,
							 MessageSource messageSource,  AllinoneCatalog allinoneCatalog) {

		this.computerCatalog = computerCatalog;
		this.inventory = inventory;
		this.allinoneCatalog= allinoneCatalog;
		this.businessTime = businessTime;
		this.messageSourceAccessor = new MessageSourceAccessor(messageSource);
		this.partsinventory = partsinventory;
		this.partsCatalog = partsCatalog;

	}


	/**
	 *
	 * Shows laptops available for sell.
	 *
	 * @param model contains a list of all laptops
	 * @return template "laptop"
     */
	@RequestMapping("/laptop")
	public String notebookCatalog(Model model) {

		model.addAttribute("catalog", computerCatalog.findByType(ArticleType.NOTEBOOK));


		return "laptop";
	}

	/**
	 *
	 * Shows customizable all-in-one computers available for sell.
	 *
	 * @param model contains a list of all all-in-one-computers
	 * @return template "allinone"
     */
	@RequestMapping("/allinone")
	public String computerCatalog(Model model) {

		model.addAttribute("catalog", allinoneCatalog.findAll());



		return "allinone";
	}

	/**
	 * Shows software available for sell.
	 *
	 * @param model contains a list of software
	 * @return template "software"
     */
	@RequestMapping("/software")
	public String softwareCatalog(Model model) {

		model.addAttribute("catalog", computerCatalog.findByType(ArticleType.SOFTWARE));


		return "software";
	}

	/**
	 *
	 * Shows supplies avaiable for sell.
	 *
	 * @param model contains a list of supplies
	 * @return template "zubehoer"
     */

	@RequestMapping("/zubehoer")
	public String zubeCatalog(Model model) {

		model.addAttribute("catalog", computerCatalog.findByType(ArticleType.ZUBE));


		return "zubehoer";
	}


	/**
	 *
	 * Shows an overview with all four article categories.
	 *
	 * @return template "shopoverview"
     */
	@RequestMapping("/shop")
	public String shopoverview() {
		return "shopoverview";
	}



	@PreAuthorize("hasAnyRole('ROLE_EMPLOYEE', 'ROLE_BOSS')")
	@RequestMapping(value = "/stockdelete", method = RequestMethod.POST)
	public String stockdelete(@RequestParam("sid") Product article, ModelMap modelMap) {
		Optional<InventoryItem> item = inventory.findByProductIdentifier(article.getIdentifier());
		InventoryItem i = item.get();

		inventory.delete(i);
		if (article.getClass()==Article.class) {computerCatalog.delete(article.getId());}
			if (article.getClass()==Part.class){partsCatalog.delete(article.getId());}
				if (article.getClass()==Computer.class){allinoneCatalog.delete(article.getId());}
		modelMap.addAttribute("stock", inventory.findAll());
		return "stock";
	}

	/**
	 *
	 * Shows detail page for notebooks, supplies and software.
	 *
	 * @param article requested article
	 * @param model contains the article, the quantity and the indicator "orderable"
     * @return template "detail"
     */
	@RequestMapping("/detail/{pid}")
	public String detail(@PathVariable("pid") Article article, Model model) {

		Optional<InventoryItem> item = inventory.findByProductIdentifier(article.getIdentifier());
		Quantity quantity = item.map(InventoryItem::getQuantity).orElse(NONE);

		model.addAttribute("article", article);
		model.addAttribute("quantity", quantity);
		model.addAttribute("orderable", quantity.isGreaterThan(NONE));

		return "detail";
	}


	/**
	 *
	 * Shows detail page for customizable all-in-one computers.
	 *
	 * @param article requested article
	 * @param model contains the single components (CPU, hard drive, RAM, graphics), the package price, the base article, the quantity and the indicator "orderable"
     * @return template "compudetail"
     */
	@RequestMapping("/compudetail/{pid}")
	public String compudetail(@PathVariable("pid") Computer article, Model model) {
		Money i = article.getPrice();
		article.getProzessor().clear();
		article.getRam().clear();
		article.getHdd().clear();
		article.getGraka().clear();
		boolean p = false;
		Optional<InventoryItem> item = inventory.findByProductIdentifier(article.getIdentifier());
		Quantity quantity = item.map(InventoryItem::getQuantity).orElse(NONE);
		model.addAttribute("gesamtpreis", i);
		model.addAttribute("processor", partsCatalog.findByType(Part.PartType.PROCESSOR));
		model.addAttribute("graphic", partsCatalog.findByType(Part.PartType.GRAPHC));
		model.addAttribute("harddrive", partsCatalog.findByType(Part.PartType.HARDD));
		model.addAttribute("ram", partsCatalog.findByType(Part.PartType.RAM));
		model.addAttribute("article", article);
		model.addAttribute("quantity", quantity);
		model.addAttribute("orderable", quantity.isGreaterThan(NONE) && p);

		return "compudetail";
	}


	/**
	 *
	 * Customizes an all-in-one computer.
	 *
	 * @param part part which gets selected
	 * @param article all-in-one computer which gets customized
	 * @param modelMap contains the base article, the package price, article ID, the components (CPU, hard drive, RAM, graphics), the quantity, the indicator "orderable" and a small check if all components are selected
     * @return template "compudetail"
     */
	@RequestMapping(value = "/change", method= RequestMethod.POST)
	public String changeprocessor(@RequestParam("part") Part part, @RequestParam("pid") Computer article, ModelMap modelMap) {
		Optional<InventoryItem> item = inventory.findByProductIdentifier(article.getIdentifier());
		Optional<InventoryItem> processor = partsinventory.findByProductIdentifier(part.getIdentifier());
		Money i = article.getPrice();
		BigDecimal b = new BigDecimal(0.00);
		 CurrencyUnit c =  i.getCurrency();
		Money p = Money.of(b,c);
		Money g = Money.of(b,c);
		Money h = Money.of(b,c);
		Money r = Money.of(b,c);
		boolean n = false;
		if (article.getProzessor().isEmpty() != true ) {p = article.getProzessor().get(0).getPrice();}
		if (article.getGraka().isEmpty() != true ) {g = article.getGraka().get(0).getPrice();}
		if (article.getHdd().isEmpty() != true ) {h = article.getHdd().get(0).getPrice();}
		if (article.getRam().isEmpty() != true ) {r = article.getRam().get(0).getPrice();}


		if (part.getType()==Part.PartType.PROCESSOR){
					article.getProzessor().clear();
					article.setProzessor(part);
					p = article.getProzessor().get(0).getPrice();

				}
		if (part.getType()==Part.PartType.GRAPHC){
					article.getGraka().clear();
					article.setGraka(part);
			g = article.getGraka().get(0).getPrice();
				}
		if (part.getType()==Part.PartType.HARDD){
					article.getHdd().clear();
					article.setHdd(part);
			h = article.getHdd().get(0).getPrice();
		}
		if (part.getType()==Part.PartType.RAM){
					article.getRam().clear();
					article.setRam(part);
			r = article.getRam().get(0).getPrice();
				}
		Money gesamt = i.add(p.add(g.add(h.add(r))));
		allinoneCatalog.save(article);
		Quantity quantity = item.map(InventoryItem::getQuantity).orElse(NONE);


		modelMap.addAttribute("gesamtpreis",gesamt);
		modelMap.addAttribute("article", article);
		modelMap.addAttribute("id", article.getId());

		if (article.getProzessor().isEmpty() != true ){
			if (article.getGraka().isEmpty() != true ){
				if (article.getHdd().isEmpty() != true ){
					if (article.getRam().isEmpty() != true ){
						 n = true;
					}
				}
			}
		}
		else{
			 n = false;
		}



		modelMap.addAttribute("processor", partsCatalog.findByType(Part.PartType.PROCESSOR));
		modelMap.addAttribute("graphic", partsCatalog.findByType(Part.PartType.GRAPHC));
		modelMap.addAttribute("harddrive", partsCatalog.findByType(Part.PartType.HARDD));
		modelMap.addAttribute("ram", partsCatalog.findByType(Part.PartType.RAM));
		modelMap.addAttribute("quantity", quantity);
		modelMap.addAttribute("isok", n);
		modelMap.addAttribute("orderable", quantity.isGreaterThan(NONE)&& n);



		return "compudetail";
	}

}


