package computech.controller;

import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import javax.transaction.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import computech.AbstractWebIntegrationTests;
import computech.model.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.UserAccountManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import computech.Application;

public class SellControllerIntegrationTests extends AbstractWebIntegrationTests {

	@Autowired ComputerCatalog computerCatalog;
	@Autowired CustomerRepository customerRepository;
	@Autowired SellRepository sellRepository;
	@Autowired SellanwserRepository sellanwserRepository;
	@Autowired UserAccountManager userAccountManager;

	@Test
	public void showSellFormularTest() throws Exception {


		Optional<UserAccount> ua4= userAccountManager.findByUsername("Pgretel");





		mvc.perform(get("/sellconfirmation/{id}",ua4.get().getId())
				.with(user("boss")
						.roles("BOSS")))
		.andExpect(status().is4xxClientError());
	}
	
	/* @Test
	public void sellFormTest() throws Exception{
		
		mvc.perform(MockMvcRequestBuilders.post("/sell").with(SecurityMockMvcRequestPostProcessors.user("Pgretel"))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("articleType", "ZUBE")
				.param("article", "USB 3.0-Kabel")
				.param("description", "TEST")
				.param("condition", "Gebrauchsspuren (~55% Originalpreis)"))
		.andExpect(MockMvcResultMatchers.status().is3xxRedirection());
	} */
}
