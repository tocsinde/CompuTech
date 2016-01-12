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


/**
 *
 * Contains every all-in-one computer in the shop.
 *
 */

public interface ComputerCatalog extends Catalog <Article> {
	Iterable<Article> findByType(Article.ArticleType type);

}
