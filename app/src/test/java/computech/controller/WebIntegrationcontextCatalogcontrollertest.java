package computech.controller;

/**
 * Created by Anwender on 10.12.2015.
 */
import computech.AbstractWebIntegrationTests;
import computech.Application;
import computech.model.ComputerCatalog;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.salespointframework.inventory.InventoryItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ContextConfiguration(classes = Catalogcontrollercontext.class)

public class WebIntegrationcontextCatalogcontrollertest extends AbstractWebIntegrationTests {
    @Autowired
    protected ComputerCatalog Computercatalogmock;
    @Autowired
    protected InventoryItem invetoryItemmock;

    @Before
    public void setUp() {

        Mockito.reset(Computercatalogmock);
        Mockito.reset(invetoryItemmock);

    }
}
