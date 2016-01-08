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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import computech.AbstractWebIntegrationTests;
import computech.model.Article;
import org.junit.Test;
import org.salespointframework.catalog.ProductIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;





public class CatalogControllerIntegrationTests extends AbstractWebIntegrationTests {

	@Autowired CatalogController controller;



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
	public void detailIntegrationTest() throws Exception{
		Model model = new ExtendedModelMap();
		ProductIdentifier id = inventoryItemmock.getProduct().getId(); // mit mockito
		Article article = (Article) model.asMap().get("article"); //ohne Mockito

		mvc.perform(get("/detail{id}", id));

	}

}
