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
package computech.model;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Iterator;

import org.javamoney.moneta.Money;
import org.junit.Test;
import org.salespointframework.core.Currencies;
import org.springframework.beans.factory.annotation.Autowired;

import computech.AbstractIntegrationTests;
import computech.model.Article.ArticleType;


public class VideoCatalogIntegrationTests extends AbstractIntegrationTests {

	@Autowired ComputerCatalog catalog;

	@Test
	public void findsAllComputer() {

		Iterable<Article> result = catalog.findByType(ArticleType.COMPUTER);

		assertThat(result, is(iterableWithSize(2)));
	}

	@Test
	public void lazyLoadExceptionTest() {

		Iterable<Article> result = catalog.findByType(ArticleType.COMPUTER);
		assertThat(result, is(iterableWithSize(2)));

		Article comp = new Article("TestPC", "Image", Money.of(BigDecimal.TEN, Currencies.EURO), "d2", ArticleType.COMPUTER);
		catalog.save(comp);

		result = catalog.findByType(ArticleType.COMPUTER);
		assertThat(result, is(iterableWithSize(3)));

		Iterator<Article> it = result.iterator();
		while (it.hasNext()) {
			Article d = it.next();
			assertThat(d.getType(), is(ArticleType.COMPUTER));

			Iterable<String> iterable = d.getCategories();
			assertThat(iterable, is(iterableWithSize(0)));
		}

		assertThat(result, hasItem(comp));
	}
}
