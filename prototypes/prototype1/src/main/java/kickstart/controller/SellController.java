package kickstart.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@PreAuthorize("hasRole('ROLE_PCUSTOMER')")
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
