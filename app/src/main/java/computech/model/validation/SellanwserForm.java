package computech.model.validation;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import javax.validation.constraints.DecimalMin;

import computech.model.Article;

/**
 *
 * Validates a sell answer form.
 *
 */

public class SellanwserForm {
	
		private Article article;
		
		private Long id_costumer;

		@NotEmpty(message = "Geben Sie eine Antwort ein.")
		private String anwser;
		
		@DecimalMin(value="0.00", message="Der Preis muss positiv sein.")
		@NotNull (message = "Geben Sie einen Ankaufpreis an.")
		private BigDecimal priceoffer;
		
		
		public Article getArticle() {
			return article;
		}
		
		public Long getId_Costumer() {
			return id_costumer;
		}
		
		public void setId_Costumer(Long id_costumer) {
			this.id_costumer = id_costumer;
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
		
		public BigDecimal getPriceoffer() {
			return priceoffer;
		}
		
		public void setPriceoffer(BigDecimal priceoffer) {
			this.priceoffer = priceoffer;
		}
}
