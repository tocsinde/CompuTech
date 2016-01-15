package computech.controller;

import computech.model.ComputerCatalog;
import static org.mockito.Mockito.mock;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;


/**
 * Created by Anwender on 10.12.2015.
 */
@Configuration
@Profile("useMocks")

public class Catalogcontrollercontext {

    public ComputerCatalog computerCatalog = mock(ComputerCatalog.class);


    @Bean
    @Primary
    public Inventory inventory(){
        return mock(Inventory.class);
    }
    @Bean
    @Primary
    public InventoryItem inventoryitem(){
        return mock(InventoryItem.class);
    }

}
