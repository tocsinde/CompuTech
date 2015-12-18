package computech.controller;

import computech.model.ComputerCatalog;
import computech.model.CustomerRepository;
import computech.model.RepairRepository;
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
 * Created by Anna on 18.12.2015.
 */

@PreAuthorize("hasAnyRole('ROLE_EMPLOYEE','ROLE_BOSS')")
@Controller
public class SupportListController {

        private final CustomerRepository customerRepository;
        private final ComputerCatalog computerCatalog;
        private final RepairRepository repairRepository;
        @Autowired
        public SupportListController(CustomerRepository customerRepository, ComputerCatalog computerCatalog, RepairRepository repairRepository){

            Assert.notNull(customerRepository, "CustomerRepository must not be null!");
            Assert.notNull(computerCatalog, "ComputerCatalog must not be null!");
            Assert.notNull(repairRepository, "RepairRepository must not be null!");

            this.customerRepository = customerRepository;
            this.computerCatalog = computerCatalog;
            this.repairRepository = repairRepository;

        }



    @RequestMapping(value = "/support_list")
    public String supportList(ModelMap modelMap){

        /*Iterator<Reparation> iterator = repairRepository.findAll().iterator();
        while (iterator.hasNext()) {*/
        modelMap.addAttribute("reparations", repairRepository.findAll());


        return "support_list";
    }


    @RequestMapping(value = "/price", method = RequestMethod.POST)
    public String setPrice(ModelMap modelMap, @RequestParam("price") String priceText) {

        Money price = Money.of(new BigDecimal(priceText), Currencies.EURO);


        return "support_list";
    }
}
