package computech.controller;

import computech.AbstractIntegrationTests;
import computech.AbstractWebIntegrationTests;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.ModelMap;

import javax.transaction.Transactional;
import javax.validation.constraints.Null;

/**
 * Created by Anwender on 15.01.2016.
 */
@Transactional
public class BosscontrollerTests extends AbstractWebIntegrationTests {
    @Autowired
    BossController controller;

             	@Test(expected = AuthenticationException.class)
     	public void rejectsUnauthenticatedAccessToController() {
        		controller.customers(new ExtendedModelMap());
        	}




    @Test
    public void customertest() throws Exception {
		mvc.perform(get("/customers").with(user("boss").roles("BOSS")))
				.andExpect(status().isOk())
				.andExpect(model().attribute("customerList",is(notNullValue())))
				.andExpect(view().name("customers"));



    }
    }


