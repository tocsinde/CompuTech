package computech.controller;

import java.nio.file.attribute.UserDefinedFileAttributeView;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import computech.Application;
import computech.model.Customer;
import computech.model.CustomerRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringApplicationConfiguration(classes = Application.class)
@Transactional
public class SellControllerIntegrationTests {

	@Autowired
	private WebApplicationContext webApplicationContext;
	
	@Autowired
	private FilterChainProxy filterChainProxy;
	
	private MockMvc mockMvc;
	
	@Before
	public void setUp() {
		
		webApplicationContext.getServletContext().setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, webApplicationContext);
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilter(filterChainProxy).build();
	}
	
	@Test
	public void showSellFormularTest() throws Exception {
		
		mockMvc.perform(MockMvcRequestBuilders.get("/sell/ZUBE"))
		.andExpect(MockMvcResultMatchers.status().isOk());
	}
	
	@Test
	public void sellFormTest() throws Exception{
		
		mockMvc.perform(MockMvcRequestBuilders.post("/sell").with(SecurityMockMvcRequestPostProcessors.user("Pgretel"))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("articleType", "ZUBE")
				.param("article", "d1")
				.param("description", "TEST")
				.param("condition", "Gebrauchsspuren (~55% Originalpreis)"))
		.andExpect(MockMvcResultMatchers.status().is3xxRedirection());
	}
}
