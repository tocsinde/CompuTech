package computech.controller;

import computech.model.*;
import org.javamoney.moneta.Money;
import org.salespointframework.core.Currencies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 *
 * The SupportListController contains most of the reparations functions.
 *
 */

@PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_BOSS')")
@Controller
public class SupportListController {

        private final CustomerRepository customerRepository;
        private final ComputerCatalog computerCatalog;
        private final RepairRepository repairRepository;
        private final SellRepository sellRepository;
        @Autowired
        public SupportListController(CustomerRepository customerRepository, ComputerCatalog computerCatalog, RepairRepository repairRepository, SellRepository sellRepository){

            Assert.notNull(customerRepository, "CustomerRepository must not be null!");
            Assert.notNull(computerCatalog, "ComputerCatalog must not be null!");
            Assert.notNull(repairRepository, "RepairRepository must not be null!");

            this.customerRepository = customerRepository;
            this.computerCatalog = computerCatalog;
            this.repairRepository = repairRepository;
            this.sellRepository = sellRepository;

        }

    /**
     * Show reparationslist for Boss and employee.
     *
     * @param modelMap contains List of the reparations
     * @return template "support_list"
     */

    @RequestMapping(value = "/support_list")
    public String supportList(ModelMap modelMap){

        /*Iterator<Reparation> iterator = repairRepository.findAll().iterator();
        while (iterator.hasNext()) {*/
        modelMap.addAttribute("reparations", repairRepository.findAll());


        return "support_list";
    }

    /**
     * Save price for the reparation and confirm a changing the price
     *
     * @param priceText - price, that would be given to the reparations
     * @param reparationId contains Id of thhe reparation, where price will be changed
     * @return  to template "support_list_confirmation" if customer accept the price
     */

    @RequestMapping(value = "/support_list", method = RequestMethod.POST)
    public String setPrice(@RequestParam("price") String priceText,
                           @RequestParam("reparationId") Long reparationId) {
        Reparation reparation = repairRepository.findOne(reparationId);
        Money price = Money.of(new BigDecimal(priceText), Currencies.EURO);
        reparation.setPrice(price);
        repairRepository.save(reparation);

       return "support_list_confirmation";
    }


}
