/*
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package computech.controller;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import computech.AbstractWebIntegrationTests;
import computech.TestData.Articlebuilder;
import computech.TestData.Computerbuilder;
import computech.TestData.PartBuilder;
import computech.model.*;
import org.javamoney.moneta.Money;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.*;
import org.mockito.MockitoAnnotations;
import org.salespointframework.catalog.ProductIdentifier;
import org.salespointframework.core.Currencies;
import org.salespointframework.inventory.Inventory;
import org.salespointframework.inventory.InventoryItem;
import org.salespointframework.quantity.Quantity;
import org.salespointframework.time.BusinessTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.ui.ModelMap;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.Optional;

public class CatalogControllerIntegrationTests extends AbstractWebIntegrationTests {


	@Autowired
	CatalogController controller;
	@Autowired
	ComputerCatalog computerCatalog;
	@Autowired
	PartsCatalog partscatalog;
	@Autowired
	AllinoneCatalog allinoneCatalog;

	Inventory inventory=mock(Inventory.class);


	@Test
	public void notebookCatalogcatalogTest() throws Exception {

		mvc.perform(get("/laptop"))
				.andExpect(model().attribute("catalog", is(not(emptyIterable()))));

	}

	@Test
	public void AllinoneCatalogcatalogTest() throws Exception {

		mvc.perform(get("/allinone"))
				.andExpect(view().name("allinone"))
				.andExpect(model().attribute("catalog", is(not(emptyIterable()))));

	}

	@Test
	public void zubehoerCatalogcatalogTest() throws Exception {

		mvc.perform(get("/zubehoer"))
				.andExpect(model().attribute("catalog", is(not(emptyIterable()))));

	}

	/**
	 * Integration test for an individual controller.
	 */
	@Test
	@SuppressWarnings("unchecked")
	public void laptopControllerIntegrationTest() {

		Model model = new ExtendedModelMap();
		String returnedView = controller.notebookCatalog(model);
		assertThat(returnedView, is("laptop"));
		Iterable<Object> object = (Iterable<Object>) model.asMap().get("catalog");
		assertThat(object, is(iterableWithSize(2)));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void zubehoerControllerIntegrationTest() {
		Model model = new ExtendedModelMap();
		String returnedView = controller.zubeCatalog(model);
		assertThat(returnedView, is("zubehoer"));
		Iterable<Object> object = (Iterable<Object>) model.asMap().get("catalog");
		assertThat(object, is(iterableWithSize(2)));

	}

	@Test
	public void softwareCatalogcatalogTest() throws Exception {

		mvc.perform(get("/software"))
				.andExpect(model().attribute("catalog", is(not(emptyIterable()))));
	}

	@Test
	public void shopoverviewTest() throws Exception {

		mvc.perform(get("/shop"))
				.andExpect(view().name("shopoverview"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void softwareControllerIntegrationTest() {

		Model model = new ExtendedModelMap();
		String returnedView = controller.softwareCatalog(model);
		assertThat(returnedView, is("software"));
		Iterable<Object> object = (Iterable<Object>) model.asMap().get("catalog");

		assertThat(object, is(iterableWithSize(2)));
	}

	@Test
	public void detailIntegrationTest() throws Exception {
		Model model = new ExtendedModelMap();

		Article article = new Articlebuilder()
				.withname("test")
				.withimage("a1")
				.withmodel("ab")
				.withprice(Money.of(BigDecimal.TEN, Currencies.EURO))
				.withtype(Article.ArticleType.NOTEBOOK)
				.build();
		ProductIdentifier id = article.getId();

		computerCatalog.save(article);
		Iterable<Article> result = computerCatalog.findByType(Article.ArticleType.NOTEBOOK);
		assertThat(result, is(iterableWithSize(3)));


		String returnedView = controller.detail(article, model);
		assertThat(returnedView, is("detail"));

	}

	@Test
	public void changetest() throws Exception {
		ModelMap model = new ExtendedModelMap();
		Part part = new PartBuilder()
				.withname("test1")
				.withimage("t1")
				.withmodel("t1")
				.withprice(Money.of(BigDecimal.TEN, Currencies.EURO))
				.withtype(Part.PartType.PROCESSOR)
				.build();
		partscatalog.save(part);
		Computer all = new Computerbuilder()
				.withname("test2")
				.withimage("t2")
				.withmodel("t2")
				.withprice(Money.of(BigDecimal.TEN, Currencies.EURO))
				.withtype(Computer.Computertype.COMPUTER)
				.build();
		allinoneCatalog.save(all);
InventoryItem i=new InventoryItem(part,Quantity.of(10));
		InventoryItem b=new InventoryItem(part,Quantity.of(10));
		inventory.save(i);
		inventory.save(b);
		String returnedView = controller.changeprocessor(part,all,model);
		assertThat(returnedView, is("compudetail"));
	}
}