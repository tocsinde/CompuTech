package computech.controller;

import computech.model.ComputerCatalog;
import static org.mockito.Mockito.mock;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Created by Anwender on 10.12.2015.
 */
@Configuration
public class Catalogcontrollercontext {
    @Bean
    public ComputerCatalog computerCatalog(){
        return mock(ComputerCatalog.class);
    }

    @Bean
    public InventoryItem inventoryItem(){
        return mock(InventoryItem.class);
    }

}
