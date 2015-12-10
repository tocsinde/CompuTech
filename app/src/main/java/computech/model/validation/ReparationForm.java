package computech.model.validation;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by Anna on 08.12.2015.
 */
public class ReparationForm {

        @NotEmpty(message = "Der Article darf nicht leer sein.")
        private String article;

        @NotEmpty(message = "Der Description darf nicht leer sein.")
        private String description;

        @NotEmpty(message = "Die Model darf nicht leer sein.")
        private String model;

    public void setArticle(String article) {
        this.article = article;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getArticle() {

        return article;
    }

    public String getDescription() {
        return description;
    }

    public String getModel() {
        return model;
    }
}
