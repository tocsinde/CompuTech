package computech.model.validation;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

import computech.model.Article;

/**
 *
 * Validates a sell answer form.
 *
 */

public class SellanwserForm {
	
		private Article article;

		@NotEmpty(message = "Geben Sie eine Antwort ein.")
		private String anwser;
		
		@NotNull (message = "Geben Sie einen Ankaufpreis an.")
		private double priceoffer;
		
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
		
		public double getPriceoffer() {
			return priceoffer;
		}
		
		public void setPriceoffer(double priceoffer) {
			this.priceoffer = priceoffer;
		}
}
