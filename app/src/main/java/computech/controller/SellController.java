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
import computech.model.validation.SellanwserForm;
import computech.model.validation.customerEditForm;

import java.util.Optional;

import javax.validation.Valid;

import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@PreAuthorize("hasAnyRole('ROLE_PCUSTOMER', 'ROLE_EMPLOYEE', 'ROLE_BOSS')")
@SessionAttributes("sell")
public class SellController {
	
	private final ComputerCatalog computerCatalog;
	private final CustomerRepository customerRepository;
	private final SellRepository sellRepository;
	private final SellanwserRepository sellanwserRepository;

	@Autowired
	public SellController(ComputerCatalog computerCatalog, CustomerRepository customerRepository, SellRepository sellRepository, SellanwserRepository sellanwserRepository) {
		Assert.notNull(computerCatalog, "computerCatalog must not be null!");
		Assert.notNull(customerRepository, "customerRepository must not be null!");
		Assert.notNull(sellRepository, "sellRepository must not be null!");
		Assert.notNull(sellanwserRepository, "sellanwserRepository must not be null!");
		this.computerCatalog = computerCatalog;
		this.customerRepository = customerRepository;
		this.sellRepository = sellRepository;
		this.sellanwserRepository = sellanwserRepository;
	}

	@RequestMapping(value = "/sell")
    public String showSellFormular(ModelMap modelMap, Model model, @LoggedIn Optional<UserAccount> userAccount){
		
		modelMap.addAttribute("sellForm", new SellForm());
        modelMap.addAttribute("articletypes", Article.ArticleType.values());
		Customer customer = customerRepository.findByUserAccount(userAccount.get());
		model.addAttribute("customer", customer);

        for (Article.ArticleType articleType : Article.ArticleType.values()) {
        	
            modelMap.addAttribute(articleType.toString(), computerCatalog.findByType(articleType));
        }
        
        return "sell";
	}
	
	@RequestMapping(value = "/sell/{articleType}")
    public String showSellFormular(@PathVariable("articleType") Article.ArticleType articleType,ModelMap modelMap, Model model, @LoggedIn Optional<UserAccount> userAccount){

		Customer customer = customerRepository.findByUserAccount(userAccount.get());
		model.addAttribute("customer", customer);

		modelMap.addAttribute("sellForm", new SellForm());
        modelMap.addAttribute("articletypes", Article.ArticleType.values());
        modelMap.addAttribute("selectedarticleType", articleType);
        modelMap.addAttribute("articles", computerCatalog.findByType(articleType));

        return "sell";
    }
		
	@RequestMapping(value = "/sell", method = RequestMethod.POST)
	public String addtoResell(@ModelAttribute("sellForm") @Valid SellForm sellForm, BindingResult result,  @LoggedIn Optional<UserAccount> userAccount, ModelMap modelmap) {
		
		modelmap.addAttribute("articletypes", Article.ArticleType.values());
		for (Article.ArticleType articleType : Article.ArticleType.values()) {
			 modelmap.addAttribute(articleType.toString(), computerCatalog.findByType(articleType));
		}
		
		if (result.hasErrors()) {
			return "sell";
		}
		
		Customer customer = customerRepository.findByUserAccount(userAccount.get());
		modelmap.addAttribute("customer", customer);
		SellOrder sellorder = new SellOrder(customer, sellForm.getArticleType(), sellForm.getArticle(), sellForm.getDescription(), sellForm.getCondition());
		sellRepository.save(sellorder); 
		
			return "sellconfirmation";
	}
	
	@RequestMapping("/sellconfirmation/{id}")
	public String getSellanwser(@PathVariable("id") Long id, Model model, ModelMap modelmap) {
				
		modelmap.addAttribute("sellanwserForm", new SellanwserForm());
		Customer customer_found = customerRepository.findOne(id);
		
		model.addAttribute("customer", customer_found);
		
		
		return "sellconfirmation";
	}

}
