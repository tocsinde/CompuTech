package computech.model.validation;

import org.hibernate.validator.constraints.NotEmpty;
import computech.model.Article.*;
import computech.model.*;

public class SellForm {

	@NotEmpty(message = "Sie müssen einen Artikeltyp auswählen.")
	private ArticleType articleType;

	@NotEmpty(message = "Sie müssen einen Artikel auswählen.")
	private Article article;
	
	@NotEmpty(message = "Sie müssen eine Beschreibung für Ihren Artikel angeben.")
	private String description;
	
	public ArticleType getArticleType() {
		return articleType;
	}

	public void setArticleType(ArticleType articletype) {
		this.articleType = articletype;
	}

	public Article getArticle() {
		return article;
	}

	public void setArticle(Article article) {
		this.article = article;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

}
