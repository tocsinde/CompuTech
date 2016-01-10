package computech.model.validation;

import org.hibernate.validator.constraints.NotEmpty;

import computech.model.Article;

public class SellanwserForm {
	
		private Article article;

		@NotEmpty(message = "Geben Sie eine Antwort ein.")
		private String anwser;
		
		public Article getArticle() {
			return article;
		}
		
		public void setArticle(Article article) {
			this.article = article;
		}		
		
		public String getAnwser() {
			return anwser;
		}
		
		public void setAnwser(String anwser) {
			this.anwser = anwser;
		}
}
