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
import org.salespointframework.catalog.Product;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.order.Cart;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManager;
import org.salespointframework.payment.Cash;
import org.salespointframework.quantity.Quantity;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Optional;


@Controller
@PreAuthorize("isAuthenticated()")
@SessionAttributes("cart")
class CartController {

    private final OrderManager<Order> orderManager;
    private final Inventory<InventoryItem> inventory;
    private final PartsCatalog partsCatalog;


    private static final Quantity NONE = Quantity.of(0);
    private Optional<UserAccount> userAccount;
    private ModelMap modelMap;
    private CustomerRepository customerRepository;
    private UserAccountManager userAccountManager;

    @Autowired
    public CartController(OrderManager<Order> orderManager, Inventory<InventoryItem> inventory,PartsCatalog partsCatalog,CustomerRepository customerRepository,UserAccountManager userAccountManager) {

        Assert.notNull(orderManager, "OrderManager must not be null!");
        this.orderManager = orderManager;
        this.inventory = inventory;
        this.partsCatalog= partsCatalog;
        this.customerRepository=customerRepository;
        this.userAccountManager=userAccountManager;

    }


    @ModelAttribute("cart")
    public Cart initializeCart() {
        return new Cart();
    }


    @RequestMapping(value = "/cart", method = RequestMethod.POST)
    public String addarticle(@RequestParam("pid") Article article, @RequestParam("number") int number, @ModelAttribute Cart cart, Model model,  RedirectAttributes success) {

        Optional<InventoryItem> item = inventory.findByProductIdentifier(article.getIdentifier());

        Quantity quantity = item.map(InventoryItem::getQuantity).orElse(NONE);
        BigDecimal amount1 = quantity.getAmount();  // Herrje, wer das schöner schreiben will, kann das gerne machen
        int i = amount1.intValue();                 // Endlich funktioniert die Validierung, besser als beim
        int amount = number;                        // Videoshop :P Kevin
        if (number <= 0){
            amount = 1;
        }
        if (number >= i){
            amount = i;
        }

        cart.addOrUpdateItem(article, Quantity.of(amount));

        success.addFlashAttribute("success", "Der Artikel wurde erfolgreich Ihrem Warenkorb hinzugefügt.");

        switch (article.getType()) {
            case NOTEBOOK:
                return "redirect:laptop";

            case SOFTWARE:
                return "redirect:software";
            case ZUBE:
            default:
                return "redirect:zubehoer";
        }
    }
    @RequestMapping(value = "/cart2", method = RequestMethod.POST)
    public String addcomp(@RequestParam("pid") Computer article,
                          @RequestParam("number") int number, @ModelAttribute Cart cart, Model model, RedirectAttributes success) {

        Optional<InventoryItem> item = inventory.findByProductIdentifier(article.getIdentifier());

        Quantity quantity = item.map(InventoryItem::getQuantity).orElse(NONE);
        BigDecimal amount1 = quantity.getAmount();
        int i = amount1.intValue();
        int amount = number;
        if (number <= 0){
            amount = 1;
        }
        if (number >= i){
            amount = i;
        }


        cart.addOrUpdateItem(article, Quantity.of(amount));
        cart.addOrUpdateItem( article.getProzessor().get(0), Quantity.of(amount));
        cart.addOrUpdateItem( article.getGraka().get(0), Quantity.of(amount));
        cart.addOrUpdateItem( article.getHdd().get(0), Quantity.of(amount));
        cart.addOrUpdateItem( article.getRam().get(0), Quantity.of(amount));

        article.getGraka().clear();
        article.getHdd().clear();
        article.getProzessor().clear();
        article.getRam().clear();

        success.addFlashAttribute("success", "Der Artikel wurde erfolgreich Ihrem Warenkorb hinzugefügt.");
                return "redirect:allinone";
        }


    @RequestMapping(value = "/cart", method = RequestMethod.GET)
    public String basket(@LoggedIn Optional<UserAccount> userAccount, ModelMap modelMap, Model model) {

        for (Role role : userAccount.get().getRoles()) {
            if(role.getName().equals("ROLE_EMPLOYEE")) {
                model.addAttribute("employee", userAccount.get());
                modelMap.addAttribute("customer_list", customerRepository.findAll());
            }
        }


        return "cart";
    }
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String delete(@ModelAttribute Cart cart, @RequestParam ("identification") String id, RedirectAttributes success){
        cart.removeItem(id);

        success.addFlashAttribute("success", "Der Artikel wurde erfolgreich aus Ihrem Warenkorb entfernt.");
        return "redirect:/cart";
    }

    @RequestMapping(value = "/checkout", method = RequestMethod.POST)
    public String buy(@ModelAttribute Cart cart, @LoggedIn Optional<UserAccount> userAccount, RedirectAttributes success) {

        return userAccount.map(account -> {


            Order order = new Order(account, Cash.CASH);

            cart.addItemsTo(order);

            orderManager.payOrder(order);
            orderManager.completeOrder(order);

            cart.clear();

            success.addFlashAttribute("success", "Vielen Dank für Ihre Bestellung.");

            return "redirect:/";
        }).orElse("redirect:/cart");
    }

    @RequestMapping(value = "/checkout_employee", method = RequestMethod.POST)
    public String buyforbusinesscustomer(@ModelAttribute Cart cart, @RequestParam ("bcustomer") String nickname, RedirectAttributes success) {

        Optional <UserAccount> businessCustomer = userAccountManager.findByUsername(nickname);
        return businessCustomer.map(account -> {


            Order order = new Order(account, Cash.CASH);

            cart.addItemsTo(order);

            orderManager.payOrder(order);
            orderManager.completeOrder(order);

            cart.clear();

            success.addFlashAttribute("success", "Die Bestellung für den Geschäftskunden ist abgeschlossen.");

            return "redirect:/";
        }).orElse("redirect:/cart");
    }

}
