package computech.controller;

import computech.model.ComputerCatalog;
import org.mockito.Mockito;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Created by Anwender on 10.12.2015.
 */
@Configuration
public class Catalogcontrollercontext {
    @Bean
    public ComputerCatalog computercatalog(){
        return Mockito.mock(ComputerCatalog.class);
    }
    @Bean
    public InventoryItem inventory(){
        return Mockito.mock(InventoryItem.class);
    }
}
