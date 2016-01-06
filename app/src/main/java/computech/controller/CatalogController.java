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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.money.CurrencyUnit;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

//import org.salespointframework.quantity.Units;

@Controller
class CatalogController {

	private static final Quantity NONE = Quantity.of(0);
	private final ComputerCatalog computerCatalog;
	private final PartsCatalog partsCatalog;
	private final AllinoneCatalog allinoneCatalog;
	private final Inventory<InventoryItem> partsinventory;
	private final Inventory<InventoryItem> inventory;
	private final BusinessTime businessTime;

	// (｡◕‿◕｡)
	// Da wir nur ein Catalog.html-Template nutzen, aber dennoch den richtigen Titel auf der Seite haben wollen,
	// nutzen wir den MessageSourceAccessor um an die messsages.properties Werte zu kommen
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


	@RequestMapping("/laptop")
	public String notebookCatalog(Model model) {

		model.addAttribute("catalog", computerCatalog.findByType(ArticleType.NOTEBOOK));


		return "laptop";
	}

	@RequestMapping("/allinone")
	public String computerCatalog(Model model) {

		model.addAttribute("catalog", allinoneCatalog.findAll());



		return "allinone";
	}

	@RequestMapping("/software")
	public String softwareCatalog(Model model) {

		model.addAttribute("catalog", computerCatalog.findByType(ArticleType.SOFTWARE));


		return "software";
	}

	@RequestMapping("/zubehoer")
	public String zubeCatalog(Model model) {

		model.addAttribute("catalog", computerCatalog.findByType(ArticleType.ZUBE));


		return "zubehoer";
	}


	@RequestMapping("/shop")
	public String shopoverview() {
		return "shopoverview";
	}


	@RequestMapping("/detail/{pid}")
	public String detail(@PathVariable("pid") Article article, Model model) {

		Optional<InventoryItem> item = inventory.findByProductIdentifier(article.getIdentifier());
		Quantity quantity = item.map(InventoryItem::getQuantity).orElse(NONE);

		model.addAttribute("article", article);
		model.addAttribute("quantity", quantity);
		model.addAttribute("orderable", quantity.isGreaterThan(NONE));

		return "detail";
	}

	@RequestMapping("/compudetail/{pid}")
	public String compudetail(@PathVariable("pid") Computer article, Model model, Part part) {
		Money i = article.getPrice();
		article.getProzessor().clear();
		article.getRam().clear();
		article.getHdd().clear();
		article.getGraka().clear();

		Optional<InventoryItem> item = inventory.findByProductIdentifier(article.getIdentifier());
		Quantity quantity = item.map(InventoryItem::getQuantity).orElse(NONE);
		model.addAttribute("gesamtpreis", i);
		model.addAttribute("processor", partsCatalog.findByType(Part.PartType.PROCESSOR));
		model.addAttribute("graphic", partsCatalog.findByType(Part.PartType.GRAPHC));
		model.addAttribute("harddrive", partsCatalog.findByType(Part.PartType.HARDD));
		model.addAttribute("ram", partsCatalog.findByType(Part.PartType.RAM));
		model.addAttribute("article", article);
		model.addAttribute("quantity", quantity);
		model.addAttribute("orderable", quantity.isGreaterThan(NONE));

		return "compudetail";
	}

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






		modelMap.addAttribute("processor", partsCatalog.findByType(Part.PartType.PROCESSOR));
		modelMap.addAttribute("graphic", partsCatalog.findByType(Part.PartType.GRAPHC));
		modelMap.addAttribute("harddrive", partsCatalog.findByType(Part.PartType.HARDD));
		modelMap.addAttribute("ram", partsCatalog.findByType(Part.PartType.RAM));
		modelMap.addAttribute("quantity", quantity);
		modelMap.addAttribute("orderable", quantity.isGreaterThan(NONE));



		return "compudetail";
	}

}


