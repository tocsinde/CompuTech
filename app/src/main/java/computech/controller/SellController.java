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
import computech.model.validation.SellForm;

import java.util.Optional;

import javax.validation.Valid;

import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@PreAuthorize("hasAnyRole('ROLE_PCUSTOMER', 'ROLE_BOSS', 'ROLE_EMPLOYEE')")
//@SessionAttributes("sell")
public class SellController {
	
	private final ComputerCatalog computerCatalog;
	private final CustomerRepository customerRepository;
	private final SellRepository sellRepository;

	@Autowired
	public SellController(ComputerCatalog computerCatalog, CustomerRepository customerRepository, SellRepository sellRepository) {
		Assert.notNull(computerCatalog, "computerCatalog must not be null!");
		Assert.notNull(customerRepository, "customerRepository must not be null!");
		Assert.notNull(sellRepository, "sellRepository must not be null!");
		this.computerCatalog = computerCatalog;
		this.customerRepository = customerRepository;
		this.sellRepository = sellRepository;
	}

	@RequestMapping(value = "/sell")
    public String showSellFormular(ModelMap modelMap){
		
		modelMap.addAttribute("sellForm", new SellForm());
            modelMap.addAttribute("articletypes", Article.ArticleType.values());

        for (Article.ArticleType articleType : Article.ArticleType.values()) {
            modelMap.addAttribute(articleType.toString(), computerCatalog.findByType(articleType));
        }
        
        //    modelMap.addAttribute("articletypes", Article.ArticleType.values());

        
        //    	modelMap.addAttribute("articles", computerCatalog.findByType(articletype));
        
        return "sell";
	}
	
	@RequestMapping(value = "/sell/{articleType}")
    public String showSellFormular(@PathVariable("articleType") Article.ArticleType articleType,ModelMap modelMap){

	/*	if (result.hasErrors()) {
			return "sell";
		} */
		
		modelMap.addAttribute("sellForm", new SellForm());
        modelMap.addAttribute("articletypes", Article.ArticleType.values());
        modelMap.addAttribute("selectedType", articleType);


        modelMap.addAttribute("articles", computerCatalog.findByType(articleType));

        System.out.println(customerRepository.count());
        System.out.println("Get Method");
        //  modelMap.addAttribute("catalog", computerCatalog.findByType());
        //  modelMap.addAttribute("articleList",  computerCatalog.findAll());

        return "sell";
    }
		
	@RequestMapping(value = "/sell", method = RequestMethod.POST)
	private String addtoResell(@ModelAttribute("sellForm") @Valid SellForm sellForm, BindingResult result,  @LoggedIn Optional<UserAccount> userAccount, ModelMap modelmap) {
		
		System.out.println(customerRepository.count());
		/*Customer customer = customerRepository.findByUserAccount(userAccount.get());
		
		if (result.hasErrors()) {
			return "sell";
		}
	
		SellOrder sellorder = new SellOrder(customer, sellForm.getArticleType(), sellForm.getArticle(), sellForm.getDescription());
		sellRepository.save(sellorder); */
		
			return "redirect:/";
	}
	
/*	@RequestMapping(value = "/sell", method = RequestMethod.POST)
	public String addSellArticel(@RequestBody MultiValueMap body) {

		System.out.println("###################################################################################");
		System.out.println(body);

		return "redirect:/";
	//	addtoResell(article,description);

		switch (article.getType()) {
			case NOTEBOOK:
				return "redirect:/notebookCatalog";
			case COMPUTER:
				return "redirect:/computerCatalog";
			case SOFTWARE:
				return "redirect:/softwareCatalog";
			default:
				return "redirect:/zubeCatalog";
		} 
	}  */

	/*@RequestMapping(value = "/sellout", method = RequestMethod.POST)
	public String sendrequest(@ModelAttribute Cart sell, @LoggedIn Optional<UserAccount> userAccount) {

		return userAccount.map(account -> {

			Order sellorder = new Order(account, Cash.CASH);
			
			sell.addItemsTo(sellorder);

			sellManager.payOrder(sellorder);
			sellManager.completeOrder(sellorder);

			sell.clear();

			return "redirect:/";
		}).orElse("redirect:/sell");
	} */

}
