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
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.order.Cart;
import org.salespointframework.order.Order;
import org.salespointframework.order.OrderManager;
import org.salespointframework.payment.Cash;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;


@Controller
@PreAuthorize("isAuthenticated()")
@SessionAttributes("cart")
class CartController {

    private final OrderManager<Order> orderManager;
    private final Inventory<InventoryItem> inventory;
    private static final Quantity NONE = Quantity.of(0);
    @Autowired
    public CartController(OrderManager<Order> orderManager, Inventory<InventoryItem> inventory) {

        Assert.notNull(orderManager, "OrderManager must not be null!");
        this.orderManager = orderManager;
        this.inventory = inventory;
    }


    @ModelAttribute("cart")
    public Cart initializeCart() {
        return new Cart();
    }


    @RequestMapping(value = "/cart", method = RequestMethod.POST)
    public String addDisc(@RequestParam("pid") Article article, @RequestParam("number") int number, @ModelAttribute Cart cart, Model model) {

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



        switch (article.getType()) {
            case NOTEBOOK:
                return "redirect:laptop";
            case COMPUTER:
                return "redirect:allinone";
            case SOFTWARE:
                return "redirect:software";
            case ZUBE:
            default:
                return "redirect:zubehoer";
        }
    }

    @RequestMapping(value = "/cart", method = RequestMethod.GET)
    public String basket() {
        return "cart";
    }
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String delete(@ModelAttribute Cart cart, @RequestParam ("identification") String id){
        cart.removeItem(id);
        return "redirect:/cart";
    }

    @RequestMapping(value = "/checkout", method = RequestMethod.POST)
    public String buy(@ModelAttribute Cart cart, @LoggedIn Optional<UserAccount> userAccount) {

        return userAccount.map(account -> {


            Order order = new Order(account, Cash.CASH);

            cart.addItemsTo(order);

            orderManager.payOrder(order);
            orderManager.completeOrder(order);

            cart.clear();

            return "redirect:/";
        }).orElse("redirect:/cart");
    }

}
