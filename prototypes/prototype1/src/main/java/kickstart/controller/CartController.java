

package kickstart.controller;
        import kickstart.model.Computer;
        import java.util.Optional;

        import org.salespointframework.catalog.Product;
        import org.salespointframework.core.AbstractEntity;
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


@Controller
@PreAuthorize("isAuthenticated()")
@SessionAttributes("cart")
class CartController {

    private final OrderManager<Order> orderManager;


    @Autowired
    public CartController(OrderManager<Order> orderManager) {

        Assert.notNull(orderManager, "OrderManager must not be null!");
        this.orderManager = orderManager;
    }


    @ModelAttribute("cart")
    public Cart initializeCart() {
        return new Cart();
    }


    @RequestMapping(value = "/cart", method = RequestMethod.POST)
    public String addDisc(@RequestParam("pid") Computer computer, @RequestParam("number") int number, @ModelAttribute Cart cart) {

        int amount = number <= 0 || number > 5 ? 1 : number;


        cart.addOrUpdateItem(computer, Quantity.of(amount));



        switch (computer.getType()) {
            case NOTEBOOK:
                return "redirect:laptop";
            case COMPUTER:
            default:
                return "redirect:allinone";
        }
    }

    @RequestMapping(value = "/cart", method = RequestMethod.GET)
    public String basket() {
        return "cart";
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
