package kickstart.model;

import org.salespointframework.catalog.Catalog;


public interface ComputerCatalog extends Catalog <Article> {
	Iterable<Article> findByType(Article.ArticleType type);

}
