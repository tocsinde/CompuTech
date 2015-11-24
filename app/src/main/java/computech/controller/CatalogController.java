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

import computech.model.Article;
import computech.model.Article.ArticleType;
import computech.model.ComputerCatalog;
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

import java.util.Optional;

//import org.salespointframework.quantity.Units;

@Controller
class CatalogController {

	private static final Quantity NONE = Quantity.of(0);

	private final ComputerCatalog computerCatalog;
	private final Inventory<InventoryItem> inventory;
	private final BusinessTime businessTime;

	// (｡◕‿◕｡)
	// Da wir nur ein Catalog.html-Template nutzen, aber dennoch den richtigen Titel auf der Seite haben wollen,
	// nutzen wir den MessageSourceAccessor um an die messsages.properties Werte zu kommen
	private final MessageSourceAccessor messageSourceAccessor;

	@Autowired
	public CatalogController(ComputerCatalog computerCatalog, Inventory<InventoryItem> inventory, BusinessTime businessTime,
							 MessageSource messageSource) {

		this.computerCatalog = computerCatalog;
		this.inventory = inventory;
		this.businessTime = businessTime;
		this.messageSourceAccessor = new MessageSourceAccessor(messageSource);
	}

	@RequestMapping("/articleCatalog")
	public String computerCatalog(ModelMap modelMap) {

		modelMap.addAttribute("catalog", computerCatalog.findByType(ArticleType.COMPUTER));
		modelMap.addAttribute("title", messageSourceAccessor.getMessage("catalog.COMPUTER.title"));

		return "computerCatalog";
	}

	@RequestMapping("/laptop")
	public String notebookCatalog(Model model) {

		model.addAttribute("catalog", computerCatalog.findByType(ArticleType.NOTEBOOK));


		return "laptop";
	}
	@RequestMapping("/allinone")
	public String computerCatalog(Model model) {

		model.addAttribute("catalog", computerCatalog.findByType(ArticleType.COMPUTER));


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
}



