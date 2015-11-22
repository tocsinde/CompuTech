/*
 *
 *	SWT-Praktikum TU Dresden 2015
 *	Gruppe 32 - Computech
 *
 *	Stephan Fischer
 *  Anna Gromykina
 *  Kevin Horst
 *  Philipp Oehme
 *
 */

package computech.model;

import org.salespointframework.catalog.Catalog;


public interface ComputerCatalog extends Catalog <Article> {
	Iterable<Article> findByType(Article.ArticleType type);

}
