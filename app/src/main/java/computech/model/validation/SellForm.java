package computech.model.validation;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import computech.model.Article.*;
import computech.model.*;

public class SellForm {

	@NotNull(message = "Sie müssen einen Artikeltyp auswählen.")
	private ArticleType articleType;

	@NotNull(message = "Sie müssen einen Artikel auswählen.")
	private Article article;
	
	@NotEmpty(message = "Sie müssen eine Beschreibung für Ihren Artikel angeben.")
	private String description;
	
	@NotEmpty(message = "Sie müssen den Zustand von Ihren Artikel angeben.")
	private String condition;
	
	private boolean status = true;
	
	public ArticleType getArticleType() {
		return articleType;
	}

	public void setArticleType(ArticleType articleType) {
		this.articleType = articleType;
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
	
	public String getCondition() {
		return condition;
	}
	
	public void setCondition(String condition) {
		this.condition = condition;
	}
	
	public boolean getStatus() {
		return status;
	}
	
	public void setStatus(boolean status) {
		this.status = status;
	}
	

}
