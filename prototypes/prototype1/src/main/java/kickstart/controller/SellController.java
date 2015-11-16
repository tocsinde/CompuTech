package kickstart.controller;

import kickstart.model.Computer;
import kickstart.model.Computer.ComputerType;

import java.util.Optional;

import org.salespointframework.catalog.Product;
import org.salespointframework.core.AbstractEntity;
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
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@PreAuthorize("hasRole('ROLE_PCOSTUMER')")
@SessionAttributes("sell")
public class SellController {
	
/*	@RequestMapping(value = "/sell", method = RequestMethod.POST)
	public String addArticel(@RequestParam("Computertype") ComputerType ComputerType,@RequestParam("Computer") Computer Computer, @RequestParam("number") int number, @ModelAttribute Cart Sell) {
	
		Cart.addOrUpdateItem(ComputerType, Quantity.of(number));
	} */
	@RequestMapping(value = "/sell", method = RequestMethod.GET)
	public String sellorder() {
		return "sell";
	}
	
}
