package kickstart.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

//import org.hibernate.annotations.Type;


@Entity
@Table(name = "COMMENTS")
public class Comment implements Serializable {

		// (｡◕‿◕｡)
		// Falls man die Id nicht selber setzen will, kann die mit @GeneratedValue vom JPA-Provider generiert und gesetzt
		// werden
		@Id @GeneratedValue private long id;

		private String text;
		private int rating;
		private LocalDateTime date;

		@SuppressWarnings("unused")
		private Comment() {}

		public Comment(String text, int rating, LocalDateTime dateTime) {
			this.text = text;
			this.rating = rating;
			this.date = dateTime;
		}

		public String getText() {
			return text;
		}

		public LocalDateTime getDate() {
			return date;
		}

		public int getRating() {
			return rating;
		}

		@Override
		public String toString() {
			return text;
		}
	}
